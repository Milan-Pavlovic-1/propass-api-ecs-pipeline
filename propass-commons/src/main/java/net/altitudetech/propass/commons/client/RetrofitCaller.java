package net.altitudetech.propass.commons.client;

import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.altitudetech.propass.commons.converter.retrofit.ObjectToPathMapMapper;
import net.altitudetech.propass.commons.dto.ErrorResponseDTO;
import net.altitudetech.propass.commons.exception.BaseException;
import net.altitudetech.propass.commons.exception.ForbiddenException;
import net.altitudetech.propass.commons.exception.IllegalRequestException;
import net.altitudetech.propass.commons.exception.InternalServerErrorException;
import net.altitudetech.propass.commons.exception.NotFoundException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

@Slf4j
@Service
public class RetrofitCaller {
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private ObjectToPathMapMapper objectToPathMapMapper;

  public <T> T sync(Object queryMap, Function<Map<String, Object>, Call<T>> call) {
    Map<String, Object> map;
    try {
      map = this.objectToPathMapMapper.map(queryMap);
      return sync(call.apply(map));
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new InternalServerErrorException("Failed to convert queryMap object to map of paths.", e);
    }
  }

  public <T> T sync(Call<T> call) {
    String errorMessage = "An error occured.";
    int errorCode = HttpStatus.INTERNAL_SERVER_ERROR.value();

    try {
      Response<T> response = call.execute();

      if (response.isSuccessful()) {
        return response.body();
      } else {
        ResponseBody responseErrorBody = response.errorBody();

        if (responseErrorBody != null && responseErrorBody.contentLength() > 0) {
          String errorBody = responseErrorBody.string();
          ErrorResponseDTO errorResponse =
              this.objectMapper.readValue(errorBody, ErrorResponseDTO.class);
          errorMessage = errorResponse.getMessage();
        }

        errorCode = response.code();
      }
    } catch (Exception ex) {
      log.error("Error occured while doing retrofit call.", ex);
    }

    throw getException(errorCode, errorMessage);
  }

  private BaseException getException(int errorCode, String message) {
    if (HttpStatus.NOT_FOUND.value() == errorCode) {
      return new NotFoundException(message);
    } else if (HttpStatus.INTERNAL_SERVER_ERROR.value() == errorCode) {
      return new InternalServerErrorException(message);
    } else if (HttpStatus.BAD_REQUEST.value() == errorCode) {
      return new IllegalRequestException(message);
    } else if (HttpStatus.FORBIDDEN.value() == errorCode) {
      return new ForbiddenException(message);
    } else {
      return new InternalServerErrorException(message);
    }
  }

}
