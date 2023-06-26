package net.altitudetech.propass.api.gateway.client;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import net.altitudetech.propass.api.gateway.dto.FlightDetailsDTO;
import net.altitudetech.propass.api.gateway.dto.ValidationResponseDTO;
import net.altitudetech.propass.api.gateway.dto.VoucherDTO;
import net.altitudetech.propass.api.gateway.dto.VoucherHistoryDTO;
import net.altitudetech.propass.api.gateway.dto.VoucherTypeDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PropassVoucherClient {
  @GET("/vouchers")
  public Call<Page<VoucherDTO>> getVouchers(@Query(value = "page", encoded = true) Pageable pageable);

  @GET("/vouchers/{voucherId}")
  public Call<VoucherDTO> getVoucher(@Path(value = "voucherId") Long voucherId);

  @POST("/vouchers")
  public Call<VoucherDTO> createVoucher(@Body VoucherDTO voucher);

  @PUT("/vouchers/{voucherId}")
  public Call<VoucherDTO> updateVoucher(@Path(value = "voucherId") Long voucherId, @Body VoucherDTO voucher);

  @GET("/vouchers/{voucherId}/history")
  public Call<Page<VoucherHistoryDTO>> getVoucherHistories(@Path(value = "voucherId") Long voucherId,
      @Query(value = "page", encoded = true) Pageable pageable);

  @GET("/vouchers/{voucherId}/history/{historyId}")
  public Call<VoucherHistoryDTO> getVoucherHistory(@Path(value = "voucherId") Long voucherId,
      @Path(value = "historyId") Long historyId);
  
  @POST("/vouchers/{voucherId}/history")
  public Call<VoucherHistoryDTO> createVoucherHistory(@Path(value = "voucherId") Long voucherId, @Body VoucherHistoryDTO history);
  
  @GET("/vouchers/types/{typeId}")
  public Call<VoucherTypeDTO> getVoucherType(@Path(value = "typeId") Long typeId);
  
  @POST("/vouchers/types")
  public Call<List<VoucherTypeDTO>> getVoucherTypes(@Body FlightDetailsDTO flight);
  
  @POST("/vouchers/types/{typeId}/validate")
  public Call<ValidationResponseDTO> validateFlightForVoucherType(@Path(value = "typeId") Long typeId, @Body FlightDetailsDTO flight);
}
