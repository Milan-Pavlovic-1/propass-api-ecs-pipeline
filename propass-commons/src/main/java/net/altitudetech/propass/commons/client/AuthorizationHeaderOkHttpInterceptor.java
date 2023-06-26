package net.altitudetech.propass.commons.client;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public class AuthorizationHeaderOkHttpInterceptor implements Interceptor {

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request originalRequest = chain.request();

    ServletRequestAttributes requestAttributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      HttpServletRequest currentRequest = requestAttributes.getRequest();
      String authToken = currentRequest.getHeader("Authorization");

      if (authToken != null) {
        Request newRequest =
            originalRequest.newBuilder().header("Authorization", authToken).build();
        return chain.proceed(newRequest);
      }
    }

    return chain.proceed(originalRequest);
  }
}
