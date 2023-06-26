package net.altitudetech.propass.api.gateway.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import net.altitudetech.propass.api.gateway.dto.FlightDTO;
import net.altitudetech.propass.api.gateway.dto.UserDTO;

@Service
public class EmailService {
  @Value("${propass.vouchers.email:}")
  private String from;

  @Autowired
  private JavaMailSender emailSender;

  public void sendBoardingPassEmail(FlightDTO flight, UserDTO user, byte[] boardingPass)
      throws MessagingException {
    String html = getHtml(flight);
    String to = user.getEmail();
    sendMessageWithAttachment(to, to, html, boardingPass);
  }

  private void sendMessageWithAttachment(String to, String subject, String html, byte[] attachement)
      throws MessagingException {
    MimeMessage message = this.emailSender.createMimeMessage();

    MimeMessageHelper helper = new MimeMessageHelper(message, true);

    helper.setFrom(this.from);
    helper.setTo(to);
    helper.setSubject(DEFAULT_SUBJECT);
    helper.setText(html, true);

    helper.addAttachment("Boarding Pass.pdf", new ByteArrayResource(attachement));

    this.emailSender.send(message);
  }

  private String getHtml(FlightDTO flight) {
    return BOARDING_PASS_HTML_TEMPLATE.replace("${flightId}", flight.getId().toString())
        .replace("${from}", flight.getLocationFrom().getName())
        .replace("${to}", flight.getLocationTo().getName());
  }

  private static final String DEFAULT_SUBJECT = "Your Boarding Pass";

  private static final String BOARDING_PASS_HTML_TEMPLATE =
      """
          <!DOCTYPE html>
          <html lang="en">
          <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
              body {
                font-family: Segoe UI, sans-serif;
                font-size: 16px;
                line-height: 1.5;
                margin: 0;
                padding: 20px;
              }
              .text-logo {
                color: rgb(181, 152, 91);
                font-size: 24px;
                font-weight: bold;
                margin-bottom: 12px;
              }
              .container {
                max-width: 600px;
                margin: auto;
                padding: 30px;
              }
              .logo {
                display: block;
                margin-bottom: 20px;
              }
              .header {
                padding: 20px;
                font-size: 24px;
                font-weight: bold;
                text-align: center;
                border-radius: 8px;
                background-color: rgb(197, 197, 197);
              }
              .content {
                padding: 20px;
              }
              .flight-info {
                font-weight: bold;
                margin-bottom: 10px;
              }
            </style>
          </head>
          <body>
            <div class="container">
              <div class="text-logo">PROPASS</div>
              <div class="header">
                Your Boarding Pass
              </div>
              <div class="content">
                <p>Dear Customer,</p>
                <p>We are pleased to inform you that you have received a boarding pass from PROPASS. Please find the details of your flight below:</p>
                <p class="flight-info">Flight Number: ${flightId}</p>
                <p class="flight-info">Departure: ${from}</p>
                <p class="flight-info">Arrival: ${to}</p>
                <p>Please arrive at the airport at least 2 hours before the scheduled departure time. Make sure to carry a valid ID and your boarding pass for a smooth check-in process.</p>
                <p>We wish you a pleasant flight!</p>
                <p>Best regards,</p>
                <p>PROPASS Team</p>
              </div>
            </div>
          </body>
          </html>""";

}
