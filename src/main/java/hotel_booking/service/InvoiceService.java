package hotel_booking.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import hotel_booking.dto.request.GenerateInvoiceRequest;
import hotel_booking.dto.response.InvoiceResponse;
import hotel_booking.entity.Booking;
import hotel_booking.entity.Invoice;
import hotel_booking.entity.Payment;
import hotel_booking.repository.BookingRepository;
import hotel_booking.repository.InvoiceRepository;
import hotel_booking.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;

    private final BookingRepository bookingRepository;

    private final PaymentRepository paymentRepository;

    private final EmailService emailService;

    // =========================================================
    // GENERATE INVOICE
    // =========================================================
    @Transactional
    public InvoiceResponse generateInvoice(
            GenerateInvoiceRequest request
    ) {

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("BOOKING_NOT_FOUND"));

        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new RuntimeException("PAYMENT_NOT_FOUND"));

        // ===== CHECK EXIST =====
        if (invoiceRepository.existsByPaymentId(payment.getId())) {
            throw new RuntimeException("INVOICE_ALREADY_EXISTS");
        }

        Invoice invoice = Invoice.builder()

                // ===== BOOKING =====
                .booking(booking)

                // ===== PAYMENT =====
                .payment(payment)

                // ===== CUSTOMER =====
                .customerName(booking.getCustomerName())

                .customerEmail(booking.getCustomerEmail())

                .customerPhone(booking.getCustomerPhone())

                // ===== INVOICE =====
                .amountPaid(payment.getAmount())

                .invoiceDescription(request.getInvoiceDescription())

                .issuedAt(LocalDateTime.now())

                .isSentEmail(false)

                .build();

        invoiceRepository.save(invoice);

        return mapToResponse(invoice);
    }

    // =========================================================
    // VIEW DETAIL
    // =========================================================
    public InvoiceResponse viewInvoiceDetail(
            Integer invoiceId
    ) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                        new RuntimeException("INVOICE_NOT_FOUND"));

        return mapToResponse(invoice);
    }

    // =========================================================
    // DOWNLOAD PDF
    // =========================================================

    public byte[] downloadInvoicePdf(Integer invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("INVOICE_NOT_FOUND"));
        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);
            document.add(new Paragraph("HOTEL INVOICE"));
            String info =
                    "Invoice Number: "
                            + invoice.getInvoiceNumber() + "\n"
                            + "Customer Name: "
                            + invoice.getCustomerName() + "\n"
                            + "Customer Email: "
                            + invoice.getCustomerEmail() + "\n"
                            + "Customer Phone: "
                            + invoice.getCustomerPhone() + "\n"
                            + "Amount Paid: "
                            + invoice.getAmountPaid() + "\n"
                            + "Description: "
                            + invoice.getInvoiceDescription() + "\n"
                            + "Issued At: "
                            + invoice.getIssuedAt();

            document.add(new Paragraph(info));

            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("GENERATE_PDF_FAILED");
        }
    }

    // =========================================================
    // SEND EMAIL
    // =========================================================

    @Transactional
    public void sendInvoiceEmail(
            Integer invoiceId
    ) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("INVOICE_NOT_FOUND"));

        // ===== VALIDATE EMAIL =====
        if (invoice.getCustomerEmail() == null || invoice.getCustomerEmail().isBlank()) {

            throw new RuntimeException("CUSTOMER_EMAIL_NOT_FOUND"
            );
        }

        // ===== SEND EMAIL =====
        String content =
                """
                Dear %s,
        
                Thank you for choosing our hotel.
        
                ==============================
                         INVOICE DETAIL
                ==============================
        
                Invoice Number : %s
                Customer Name  : %s
                Customer Email : %s
                Customer Phone : %s
                Amount Paid    : %s
                Description    : %s
                Issued At      : %s
        
                ==============================
        
                Best regards,
                Hotel Booking System
                """
                        .formatted(
                                invoice.getCustomerName(),
                                invoice.getInvoiceNumber(),
                                invoice.getCustomerName(),
                                invoice.getCustomerEmail(),
                                invoice.getCustomerPhone(),
                                invoice.getAmountPaid(),
                                invoice.getInvoiceDescription(),
                                invoice.getIssuedAt()
                        );
        emailService.sendCustomerEmail(invoice.getCustomerEmail(), "BOOKING ROOM IN CHECK-X", content);

        invoice.setIsSentEmail(true);

        invoiceRepository.save(invoice);
    }

    // =========================================================
    // MAP RESPONSE
    // =========================================================
    private InvoiceResponse mapToResponse(
            Invoice invoice
    ) {

        return InvoiceResponse.builder()

                .id(invoice.getId())

                .invoiceNumber(invoice.getInvoiceNumber())

                .customerName(invoice.getCustomerName())

                .customerEmail(invoice.getCustomerEmail())

                .customerPhone(invoice.getCustomerPhone())

                .amountPaid(invoice.getAmountPaid())

                .invoiceDescription(
                        invoice.getInvoiceDescription()
                )

                .issuedAt(invoice.getIssuedAt())

                .isSentEmail(invoice.getIsSentEmail())

                .build();
    }
}