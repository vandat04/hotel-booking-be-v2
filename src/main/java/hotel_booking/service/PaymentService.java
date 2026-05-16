package hotel_booking.service;

import hotel_booking.dto.request.PaymentRequest;
import hotel_booking.entity.*;
import hotel_booking.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final RoomScheduleRepository roomScheduleRepository;
    private final VNPayService vnPayService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // ==================================
    // ========= PAYMENT BOOKING =========
    // ==================================
    @Transactional
    public String createVnPayPayment(
            Integer customerId,
            PaymentRequest request
    ) {

        Booking booking = bookingRepository.findById(
                request.getBookingId()
        ).orElseThrow(() ->
                new RuntimeException("BOOKING_NOT_FOUND")
        );

        // ===== OWNER =====
        if (booking.getCustomer() == null ||
                !booking.getCustomer().getId().equals(customerId)) {

            throw new RuntimeException("FORBIDDEN");
        }

        // ===== VALIDATE =====
        validateBookingPayment(booking);

        // ===== ALREADY PAID =====
        boolean alreadyPaid =
                paymentRepository.findByBooking_Id(booking.getId())
                        .stream()
                        .anyMatch(p ->
                                "SUCCESS".equalsIgnoreCase(p.getStatus())
                        );

        if (alreadyPaid) {
            throw new RuntimeException("BOOKING_ALREADY_PAID");
        }

        // ===== CREATE PAYMENT =====
        String txnRef =
                String.valueOf(System.currentTimeMillis());

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getTotalAmount())
                .paymentMethod("ONLINE")
                .gatewayName("VNPAY")
                .paymentType("FULL_ROOM_CHARGE")

                // DB chỉ cho SUCCESS/FAILED/REFUNDED
                .status("FAILED")

                .transactionReference(txnRef)
                .paymentDate(LocalDateTime.now())
                .notes(request.getNotes())
                .build();

        paymentRepository.save(payment);

        return vnPayService.createPaymentUrl(payment);
    }

    @Transactional
    public void handleVnPayReturn(Map<String, String> params) {

        String txnRef = params.get("vnp_TxnRef");

        String responseCode = params.get("vnp_ResponseCode");

        Payment payment = paymentRepository.findByTransactionReference(txnRef)
                .orElseThrow(() -> new RuntimeException("PAYMENT_NOT_FOUND"));

        // ===== AVOID DUPLICATE CALLBACK =====
        if ("SUCCESS".equalsIgnoreCase(payment.getStatus())) {
            return;
        }

        // ===== VALIDATE SIGNATURE =====
        boolean valid = vnPayService.validateSignature(params);

        if (!valid) {
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
            throw new RuntimeException("INVALID_SIGNATURE");
        }

        String vnpAmount = params.get("vnp_Amount");

        String expectedAmount = payment.getAmount()
                .multiply(java.math.BigDecimal.valueOf(100))
                .toBigInteger()
                .toString();

        if (!expectedAmount.equals(vnpAmount)) {
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
            throw new RuntimeException("INVALID_AMOUNT");
        }

        // ===== PAYMENT SUCCESS =====
        if ("00".equals(responseCode)) {
            payment.setStatus("SUCCESS");
            payment.setPaymentDate(LocalDateTime.now());
            paymentRepository.save(payment);
            Booking booking = payment.getBooking();

            // ===== UPDATE BOOKING =====
            booking.setPaymentStatus("PAID");

            booking.setStatus("CONFIRMED");

            booking.setUpdatedAt(LocalDateTime.now());

            bookingRepository.save(booking);

            // ===== UPDATE ROOM SCHEDULE =====
            List<RoomSchedule> schedules = roomScheduleRepository.findByBooking_Id(booking.getId());

            for (RoomSchedule rs : schedules) {
                rs.setStatus("SCHEDULED");
                rs.setUpdatedAt(LocalDateTime.now());
            }

            roomScheduleRepository.saveAll(schedules);

            // ===== CREATE INVOICE =====
            Invoice invoice = Invoice.builder()
                    .booking(booking)
                    .payment(payment)
                    .customerName(booking.getCustomerName())
                    .customerEmail(booking.getCustomerEmail())
                    .customerPhone(booking.getCustomerPhone())
                    .amountPaid(payment.getAmount())
                    .invoiceDescription("TIỀN PHÒNG")
                    .issuedAt(LocalDateTime.now())
                    .isSentEmail(false)
                    .build();
            invoiceRepository.save(invoice);

            User user = userRepository.findById(payment.getBooking().getId()).orElse(null);
            notificationService.createCustomerNotification(user, booking, "BOOKING ROOM IN CHECK-X", "Payment Success.", "PAYMENT_SUCCESS");

        } else {
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
        }
    }


    private void validateBookingPayment(
            Booking booking
    ) {
        // ===== BOOKING STATUS =====
        if (!"PENDING".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("Only pending booking can be paid");
        }

        // ===== PAYMENT STATUS =====
        if (!"UNPAID".equalsIgnoreCase(booking.getPaymentStatus())) {
            throw new RuntimeException("Booking already paid");
        }
    }
}