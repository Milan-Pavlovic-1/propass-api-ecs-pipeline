package net.altitudetech.propass.api.gateway.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;
import jakarta.mail.MessagingException;
import net.altitudetech.propass.api.gateway.client.PropassAuthClient;
import net.altitudetech.propass.api.gateway.client.PropassFlightClient;
import net.altitudetech.propass.api.gateway.client.PropassVoucherClient;
import net.altitudetech.propass.api.gateway.dto.FlightDTO;
import net.altitudetech.propass.api.gateway.dto.FlightDetailsDTO;
import net.altitudetech.propass.api.gateway.dto.UserDTO;
import net.altitudetech.propass.api.gateway.dto.ValidationResponseDTO;
import net.altitudetech.propass.api.gateway.dto.VoucherDTO;
import net.altitudetech.propass.api.gateway.dto.VoucherHistoryDTO;
import net.altitudetech.propass.api.gateway.dto.VoucherTypeDTO;
import net.altitudetech.propass.commons.client.RetrofitCaller;
import net.altitudetech.propass.commons.exception.IllegalRequestException;
import net.altitudetech.propass.commons.exception.InternalServerErrorException;

@Service
public class PropassVoucherService {
  @Autowired
  private RetrofitCaller retrofitCaller;
  @Autowired
  private PropassVoucherClient propassVoucherClient;
  @Autowired
  private PropassAuthClient propassAuthClient;
  @Autowired
  private PropassFlightClient propassFlightClient;
  @Autowired
  private BoardingPassService boardingPassService;
  @Autowired
  private EmailService emailService;

  public VoucherDTO purchase(Long id, FlightDetailsDTO flight) {
    ValidationResponseDTO validation = validate(id, flight);
    VoucherTypeDTO voucherType = this.retrofitCaller.sync(this.propassVoucherClient.getVoucherType(id));
    // TODO validate payment
    // TODO make idempotent in voucher service - depends on payment service implementation details
    if (validation.getValid()) {
      return this.retrofitCaller.sync(this.propassVoucherClient
          .createVoucher(VoucherDTO.builder().totalAmount(flight.getVouchers())
              .voucherType(voucherType).flightId(flight.getId()).build()));
    } else {
      throw new IllegalRequestException("Voucher type cannot be used for flight.");
    }
  }

  public VoucherHistoryDTO redeem(Long id, FlightDetailsDTO dto) {
    VoucherDTO voucher = findOne(id);
    // TODO user validation
    boolean valid = voucher.getTotalAmount() > voucher.getUsedAmount();
    if (valid) {
      // TODO make idempotent in voucher service - depends on ticket purchasing implementation
      // details
      // TODO integrate with purchasing tickets via Flight Booking Service (ie use the DTO)
      voucher.setUsedAmount(voucher.getUsedAmount() + 1);
      voucher = this.retrofitCaller.sync(this.propassVoucherClient.updateVoucher(voucher.getId(), voucher));
      String ticketRef = UUID.randomUUID().toString().substring(0, 8);
      VoucherHistoryDTO redeemResult =  this.retrofitCaller
          .sync(this.propassVoucherClient.createVoucherHistory(id, VoucherHistoryDTO.builder()
              .voucher(voucher).ticketRef(ticketRef).build()));
      
      sendBoardingPassEmail(ticketRef, dto);
      
      return redeemResult;
    } else {
      throw new IllegalRequestException("Voucher cannot be redeemed.");
    }
  }
  
  private void sendBoardingPassEmail(String ticketRef, FlightDetailsDTO dto) {
    // TODO move this functionality to a separate microservice
    UserDTO user = this.retrofitCaller.sync(this.propassAuthClient.me());
    FlightDTO flight = this.retrofitCaller.sync(this.propassFlightClient.getFlight(dto.getId()));
    flight.setDepartureDate(dto.getDepartureDate());
    try {
      byte[] boardingPass = this.boardingPassService.generateBoardingPass(flight, user, ticketRef);
      this.emailService.sendBoardingPassEmail(flight, user, boardingPass);
    } catch (DocumentException | IOException | WriterException | MessagingException e) {
      throw new InternalServerErrorException("Encountered issue while emailing Boarding Pass.", e);
    }
  }

  public VoucherDTO findOne(Long id) {
    return this.retrofitCaller.sync(this.propassVoucherClient.getVoucher(id));
  }

  public Page<VoucherDTO> findAll(Pageable pageable) {
    return this.retrofitCaller.sync(this.propassVoucherClient.getVouchers(pageable));
  }

  public Page<VoucherHistoryDTO> getHistory(Long voucherId, Pageable pageable) {
    return this.retrofitCaller.sync(this.propassVoucherClient.getVoucherHistories(voucherId, pageable));
  }

  public List<VoucherTypeDTO> findAllTypes(FlightDetailsDTO flight) {
    return this.retrofitCaller.sync(this.propassVoucherClient.getVoucherTypes(flight));
  }

  public ValidationResponseDTO validate(Long id, FlightDetailsDTO flight) {
    return this.retrofitCaller
        .sync(this.propassVoucherClient.validateFlightForVoucherType(id, flight));
  }

}
