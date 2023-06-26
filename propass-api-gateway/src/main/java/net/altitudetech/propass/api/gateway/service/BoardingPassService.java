package net.altitudetech.propass.api.gateway.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import net.altitudetech.propass.api.gateway.dto.FlightDTO;
import net.altitudetech.propass.api.gateway.dto.UserDTO;

@Service
public class BoardingPassService {
  private static final String DEFAULT_BOARDING_PASS_TITLE = "PROPASS BOARDING";
  private static final String DEFAULT_QR_CODE_CONTENT = "https://ui-dev.altitude-tech.net/";
  private static final int DEFAULT_QR_CODE_SIZE = 140;
  private static final int DEFAULT_LABEL_SIZE = 10;
  private static final BaseColor PROPASS_GOLD = new BaseColor(181, 152, 91);
  private static final Font PROPASS_GOLDEN_FONT =
      new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, PROPASS_GOLD);

  // TODO make this configurable per airline
  private static final DateTimeFormatter HOUR_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public byte[] generateBoardingPass(FlightDTO flight, UserDTO user, String ticketRef)
      throws DocumentException, IOException, WriterException {
    return generateBoardingPassPDF(flight, user, ticketRef);
  }

  private byte[] generateBoardingPassPDF(FlightDTO flight, UserDTO user, String ticketRef)
      throws DocumentException, IOException, WriterException {
    Document document = new Document(PageSize.A4);
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PdfWriter.getInstance(document, out);
    document.open();
    createBoardingPassPDF(document, flight, user, ticketRef);
    document.close();
    return out.toByteArray();
  }

  private void createBoardingPassPDF(Document document, FlightDTO flight, UserDTO user,
      String ticketRef) throws DocumentException, IOException, WriterException {
    addBoardingPassTitle(document);
    PdfPTable table = generateTable(document, flight, user, ticketRef);
    addQRCode(generateQRCode(), table);
    document.add(table);
  }

  private PdfPTable generateTable(Document document, FlightDTO flight, UserDTO user,
      String ticketRef) throws DocumentException {
    PdfPTable table = createDefaultTable();

    buildColumn(table, cell("Passenger Name", getPassengerName(user), 14), cell("From/To", getFromTo(flight), 24));
    buildColumn(table, cell("Ticket Ref", ticketRef, 14), cell("Boarding Time", getBoardingTime(flight), 14));
    buildColumn(table, cell("Flight Date", getFlightDate(flight), 14), cell("Seat", "13A", 14));

    return table;
  }

  private String getFlightDate(FlightDTO flight) {
    return DATE_FORMAT.format(flight.getDepartureDate());
  }

  private String getBoardingTime(FlightDTO flight) {
    return HOUR_FORMAT.format(flight.getDepartureDate());
  }

  private String getFromTo(FlightDTO flight) {
    return flight.getLocationFrom().getCode() + " > " + flight.getLocationTo().getCode();
  }

  private String getPassengerName(UserDTO user) {
    return user.getFirstName() + " " + user.getLastName();
  }

  private void addQRCode(Image qrCodeImage, PdfPTable table) {
    // Add QR code to the last column
    PdfPCell qrCodeCell = new PdfPCell(qrCodeImage, false);
    qrCodeCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    qrCodeCell.setVerticalAlignment(Element.ALIGN_TOP);
    qrCodeCell.setBorder(Rectangle.NO_BORDER);
    table.addCell(qrCodeCell);
  }

  private PdfPTable createDefaultTable() throws DocumentException {
    PdfPTable table = new PdfPTable(4);
    table.setWidthPercentage(100);
    table.setWidths(new int[] {2, 1, 1, 2});
    addSpacing(table);
    return table;
  }

  private BoardingPassCell cell(String label, String value, int size) {
    return new BoardingPassCell(label, value, size);
  }

  private void buildColumn(PdfPTable table, BoardingPassCell... cells) {
    // Create nested table with two rows
    PdfPTable nestedTable = new PdfPTable(1);
    nestedTable.setWidthPercentage(100);

    for (BoardingPassCell cell : cells) {
      addLabelAndInputToTable(nestedTable, cell);
      addSpacing(nestedTable);
    }

    // Add nested table to the main table
    PdfPCell nestedTableCell = new PdfPCell(nestedTable);
    nestedTableCell.setBorder(Rectangle.NO_BORDER);
    table.addCell(nestedTableCell);
  }

  private void addSpacing(PdfPTable nestedTable) {
    PdfPCell blankRow = new PdfPCell(new Phrase("\n"));
    blankRow.setFixedHeight(25f);
    blankRow.setColspan(4);
    blankRow.setBorder(Rectangle.NO_BORDER);
    nestedTable.addCell(blankRow);
  }

  private void addLabelAndInputToTable(PdfPTable table, BoardingPassCell cell) {
    // Create nested table with two rows
    PdfPTable nestedTable = new PdfPTable(1);
    nestedTable.setWidthPercentage(100);

    // Create label cell and add to nested table
    PdfPCell labelCell = new PdfPCell(new Phrase(cell.label, font(DEFAULT_LABEL_SIZE)));
    labelCell.setBorder(Rectangle.NO_BORDER);
    nestedTable.addCell(labelCell);

    // Create input cell with inputText and add to nested table
    PdfPCell inputCell = new PdfPCell(new Phrase(cell.value, font(cell.valueFontSize)));
    inputCell.setBorder(Rectangle.NO_BORDER);
    nestedTable.addCell(inputCell);

    // Add nested table to the main table
    PdfPCell nestedTableCell = new PdfPCell(nestedTable);
    nestedTableCell.setBorder(Rectangle.NO_BORDER);
    table.addCell(nestedTableCell);
  }

  private Font font(int size) {
    return new Font(Font.FontFamily.HELVETICA, size);
  }

  private void addBoardingPassTitle(Document document) throws DocumentException {
    Phrase propassBoarding = new Phrase(DEFAULT_BOARDING_PASS_TITLE, PROPASS_GOLDEN_FONT);
    PdfPCell propassCell = new PdfPCell(propassBoarding);
    propassCell.setBorder(Rectangle.NO_BORDER);
    propassCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    PdfPTable propassTable = new PdfPTable(1);
    propassTable.setWidthPercentage(100);
    propassTable.addCell(propassCell);
    document.add(propassTable);
  }

  private Image generateQRCode()
      throws WriterException, IOException, BadElementException, MalformedURLException {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      QRCodeWriter qrCodeWriter = new QRCodeWriter();
      BitMatrix bitMatrix = qrCodeWriter.encode(DEFAULT_QR_CODE_CONTENT, BarcodeFormat.QR_CODE,
          DEFAULT_QR_CODE_SIZE, DEFAULT_QR_CODE_SIZE);
      MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);
      Image qrCodeImage = Image.getInstance(out.toByteArray());
      return qrCodeImage;
    } catch (Exception e) {
      throw e;
    }
  }

  @AllArgsConstructor
  private static class BoardingPassCell {
    public String label;
    public String value;
    public int valueFontSize;
  }

}
