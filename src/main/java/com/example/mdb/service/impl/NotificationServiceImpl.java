package com.example.mdb.service.impl;

import com.example.mdb.entity.Booking;
import com.example.mdb.entity.Seat;
import com.example.mdb.service.NotificationService;
import com.example.mdb.utility.QRCodeGenerator;
import com.lowagie.text.pdf.BaseFont;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final QRCodeGenerator qrCodeGenerator;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${app.company.name:CinePass}")
    private String companyName;

    @Async
    @Override
    public void sendBookingConfirmation(Booking booking) {
        log.info("Initiating booking confirmation workflow | ReferenceCode: {}", booking.getReferenceCode());

        try {
            ZoneId indiaZone = ZoneId.of("Asia/Kolkata");

            String ticketDate = DateTimeFormatter.ofPattern("EEEE, dd MMM", Locale.ENGLISH).format(booking.getShow().getStartsAt().atZone(indiaZone))
                    + " | " + DateTimeFormatter.ofPattern("h:mm a").format(booking.getShow().getStartsAt().atZone(indiaZone));

            String invoiceOrdinalDate = getOrdinalDate(LocalDate.now(indiaZone));

            String seats = (booking.getSeats() != null && !booking.getSeats().isEmpty())
                    ? booking.getSeats().stream().map(Seat::getName).collect(Collectors.joining(", "))
                    : "Unassigned";

            String qrBase64 = qrCodeGenerator.generateQRCodeBase64(booking.getReferenceCode());

            String rawMovieTitle = (booking.getShow() != null && booking.getShow().getMovie() != null)
                    ? booking.getShow().getMovie().getTitle() : "Movie";
            String safeMovieName = rawMovieTitle.replaceAll("[^a-zA-Z0-9]", "_");

            String displayScreenType = "2D";
            if (booking.getShow() != null && booking.getShow().getScreen() != null && booking.getShow().getScreen().getScreenType() != null) {
                displayScreenType = booking.getShow().getScreen().getScreenType().toString().replace("TWO_D", "2D").replace("THREE_D", "3D");
            }

            Context ticketCtx = new Context();
            ticketCtx.setVariable("companyName", companyName);
            ticketCtx.setVariable("movieTitle", rawMovieTitle);
            ticketCtx.setVariable("theaterName", booking.getShow().getTheater().getName());
            ticketCtx.setVariable("theaterAddress", booking.getShow().getTheater().getAddress());
            ticketCtx.setVariable("theaterCity", booking.getShow().getTheater().getCity());
            ticketCtx.setVariable("screenName", booking.getShow().getScreen().getName());
            ticketCtx.setVariable("screenType", displayScreenType);
            ticketCtx.setVariable("showDateTime", ticketDate); // Matches new HTML
            ticketCtx.setVariable("seats", seats);
            ticketCtx.setVariable("qrCode", qrBase64);
            ticketCtx.setVariable("bookingId", booking.getReferenceCode());

            byte[] ticketPdfBytes = generatePdfFromHtml("m-ticket", ticketCtx);

            int seatCount = booking.getSeats() != null ? booking.getSeats().size() : 1;
            double bookingCharge = 30.00 * seatCount;
            double taxOnCharge = bookingCharge * 0.18;
            double totalPlatformFee = bookingCharge + taxOnCharge;

            String amountInWords = convertToWords((int)totalPlatformFee) + " Rupees And " +
                    convertToWords((int)Math.round((totalPlatformFee - (int)totalPlatformFee) * 100)) + " Paise Only";

            Context invoiceCtx = new Context();
            invoiceCtx.setVariable("companyName", companyName);
            invoiceCtx.setVariable("invoiceDate", invoiceOrdinalDate);
            invoiceCtx.setVariable("bookingId", booking.getReferenceCode());
            invoiceCtx.setVariable("orderId", booking.getRazorpayOrderId() != null ? booking.getRazorpayOrderId() : "ORD-" + System.currentTimeMillis());
            invoiceCtx.setVariable("invoiceNo", "TBC" + booking.getReferenceCode() + "I" + (int)(Math.random() * 1000));

            invoiceCtx.setVariable("fullName", booking.getUser().getFullName());
            invoiceCtx.setVariable("customerPhone", booking.getUser().getPhoneNumber() != null ? booking.getUser().getPhoneNumber() : "N/A");

            invoiceCtx.setVariable("theaterName", booking.getShow().getTheater().getName());
            invoiceCtx.setVariable("theaterAddress", booking.getShow().getTheater().getAddress());
            invoiceCtx.setVariable("theaterCity", booking.getShow().getTheater().getCity());
            invoiceCtx.setVariable("screenName", booking.getShow().getScreen().getName());

            invoiceCtx.setVariable("seatCount", String.valueOf(seatCount));
            invoiceCtx.setVariable("bookingCharge", String.format("%.2f", bookingCharge));
            invoiceCtx.setVariable("taxOnCharge", String.format("%.2f", taxOnCharge));
            invoiceCtx.setVariable("totalPlatformFee", String.format("%.2f", totalPlatformFee));
            invoiceCtx.setVariable("amountInWords", amountInWords);
            invoiceCtx.setVariable("ticketBaseAmount", String.format("%.2f", booking.getBaseAmount()));

            byte[] invoicePdfBytes = generatePdfFromHtml("invoice", invoiceCtx);

            MimeMessage message = prepareMimeMessage(booking, rawMovieTitle, safeMovieName, ticketPdfBytes, invoicePdfBytes);
            mailSender.send(message);

            log.info("District-grade confirmation dispatched | ReferenceCode: {}", booking.getReferenceCode());

        } catch (Exception e) {
            log.error("Failed to process confirmation | Error: {}", e.getMessage(), e);
        }
    }

    private String getOrdinalDate(LocalDate date) {
        int day = date.getDayOfMonth();
        String suffix = (day >= 11 && day <= 13) ? "th" : switch (day % 10) {
            case 1 -> "st"; case 2 -> "nd"; case 3 -> "rd"; default -> "th";
        };
        return day + suffix + " " + DateTimeFormatter.ofPattern("MMM, yyyy").format(date);
    }

    private String convertToWords(int n) {
        String[] units = {"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
        if (n < 20) return units[n];
        if (n < 100) return tens[n / 10] + ((n % 10 != 0) ? " " + units[n % 10] : "");
        return "Many";
    }

    private MimeMessage prepareMimeMessage(Booking booking, String rawMovieTitle, String safeMovieName, byte[] ticketPdfBytes, byte[] invoicePdfBytes) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(senderEmail);
        helper.setTo(booking.getUser().getEmail());
        helper.setSubject("🍿 Blockbuster Confirmed: " + rawMovieTitle);
        helper.setText("Hi " + booking.getUser().getFullName() + ",\n\nYour seat is locked! Reference: " + booking.getReferenceCode(), false);
        helper.addAttachment(safeMovieName + "_Ticket.pdf", new ByteArrayResource(ticketPdfBytes));
        helper.addAttachment(safeMovieName + "_Invoice.pdf", new ByteArrayResource(invoicePdfBytes));
        return message;
    }

    private byte[] generatePdfFromHtml(String templateName, Context context) throws Exception {
        String htmlContent = templateEngine.process(templateName, context);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        String fontPath = new ClassPathResource("fonts/DejaVuSans-Bold.ttf").getURL().toString();
        renderer.getFontResolver().addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);
        return outputStream.toByteArray();
    }
}