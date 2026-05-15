//package hotel_booking.service;
//
//import hotel_booking.dto.request.PayRequest;
//import hotel_booking.dto.request.PaymentRequest;
//import hotel_booking.dto.response.PaymentItemResponse;
//import hotel_booking.dto.response.PaymentSummaryResponse;
//import hotel_booking.dto.response.RevenueAdminDTO;
//import hotel_booking.dto.response.RevenueDashboardResponse;
//import hotel_booking.entity.*;
//import hotel_booking.repository.*;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class PaymentService {
//
//    private final BookingRepository bookingRepository;
//    private final PaymentRepository paymentRepository;
//    private final VNPayService vnPayService;
//    private final RoomTypeRepository roomTypeRepository;
//    private final RoomRepository roomRepository;
//    private final RoomKeyRepository roomKeyRepository;
//
//    public String createVnPayPayment(Long userId, PaymentRequest req) {
//
//        Booking booking = bookingRepository.findById(req.getBookingId())
//                .orElseThrow(() -> new RuntimeException("BOOKING_NOT_FOUND"));
//
//        if (!booking.getUserId().equals(userId)) {
//            throw new RuntimeException("FORBIDDEN");
//        }
//
//        String type = req.getPaymentType();
//
//        // 🔥 lấy payment có sẵn
//        Payment payment = paymentRepository
//                .findByBookingIdAndPaymentType(booking.getId(), type)
//                .orElseThrow(() -> new RuntimeException("PAYMENT_NOT_FOUND"));
//
//        // 🔥 chặn nếu đã thanh toán rồi
//        if ("PAID".equals(payment.getStatus())) {
//            throw new RuntimeException("PAYMENT_ALREADY_DONE");
//        }
//
//        // 🔥 generate txnRef mới mỗi lần thanh toán
//        String txnRef = String.valueOf(System.currentTimeMillis());
//
//        payment.setVnpTxnRef(txnRef);
//        payment.setMethod("VNPAY");
//        payment.setStatus("PENDING"); // reset nếu retry
//        payment.setCreatedAt(LocalDateTime.now());
//
//        paymentRepository.save(payment);
//
//        return vnPayService.createPaymentUrl(payment);
//    }
//
//    public void handleVnPayReturn(Map<String, String> params) {
//
//        String txnRef = params.get("vnp_TxnRef");
//        String responseCode = params.get("vnp_ResponseCode");
//
//        Payment payment = paymentRepository.findByVnpTxnRef(txnRef)
//                .orElseThrow(() -> new RuntimeException("PAYMENT_NOT_FOUND"));
//
//        // 🔥 chống callback nhiều lần
//        if ("PAID".equals(payment.getStatus())) return;
//
//        // 🔐 validate chữ ký
//        boolean valid = vnPayService.validateSignature(params);
//        if (!valid) throw new RuntimeException("INVALID_SIGNATURE");
//
//        // 🔥 check amount
//        String vnpAmount = params.get("vnp_Amount");
//        String expectedAmount = payment.getAmount()
//                .multiply(BigDecimal.valueOf(100))
//                .toBigInteger()
//                .toString();
//
//        if (!expectedAmount.equals(vnpAmount)) {
//            throw new RuntimeException("INVALID_AMOUNT");
//        }
//
//        if ("00".equals(responseCode)) {
//
//            payment.setStatus("PAID");
//            payment.setPaidAt(LocalDateTime.now());
//            payment.setTransactionCode(params.get("vnp_TransactionNo"));
//
//            Booking booking = bookingRepository.findById(payment.getBookingId())
//                    .orElseThrow();
//
//            // 🔥 logic chuẩn theo loại payment
//            if ("DEPOSIT".equals(payment.getPaymentType())) {
//                booking.setStatus("BOOKED");
//            }
//
//            if ("FINAL".equals(payment.getPaymentType())) {
//                // có thể check đã đủ tiền chưa
//                booking.setStatus("FINISHED");
//            }
//
//            bookingRepository.save(booking);
//
//        } else {
//            payment.setStatus("FAILED");
//        }
//
//        paymentRepository.save(payment);
//    }
//
//    @Transactional
//    public PaymentSummaryResponse calculatePayment(Long bookingId) {
//
//        // 🔥 1. Lấy booking
//        Booking booking = bookingRepository.findById(bookingId)
//                .orElseThrow(() -> new RuntimeException("Booking không tồn tại"));
//
//        // 🔥 3. Lấy tất cả payments (để hiển thị)
//        List<Payment> allPayments = paymentRepository.findByBookingId(bookingId);
//
//        // 🔥 4. Lọc payment đã thanh toán
//        List<Payment> paidPayments = allPayments.stream()
//                .filter(p -> "PAID".equals(p.getStatus()))
//                .toList();
//
//        // 🔥 5. Khởi tạo
//        BigDecimal totalPaid = BigDecimal.ZERO;
//        BigDecimal deposit = BigDecimal.ZERO;
//        BigDecimal finalPaid = BigDecimal.ZERO;
//        BigDecimal penalty = BigDecimal.ZERO;
//        BigDecimal extend = BigDecimal.ZERO;
//
//        // 🔥 6. Tính toán
//        for (Payment p : paidPayments) {
//
//            BigDecimal amount = p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO;
//
//            totalPaid = totalPaid.add(amount);
//
//            switch (p.getPaymentType()) {
//                case "DEPOSIT":
//                    deposit = deposit.add(amount);
//                    break;
//                case "FINAL":
//                    finalPaid = finalPaid.add(amount);
//                    break;
//                case "PENALTY":
//                    penalty = penalty.add(amount);
//                    break;
//                case "EXTEND":
//                    extend = extend.add(amount);
//                    break;
//            }
//        }
//
//        // 🔥 7. Tính remaining
//        BigDecimal totalPrice = booking.getTotalPrice() != null
//                ? booking.getTotalPrice()
//                : BigDecimal.ZERO;
//
//        BigDecimal remaining = totalPrice
//                .add(penalty)
//                .add(extend)
//                .subtract(totalPaid);
//
//        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
//            remaining = BigDecimal.ZERO;
//        }
//
//        // 🔥 8. Map DTO
//        List<PaymentItemResponse> paymentDTOs = allPayments.stream()
//                .map(p -> PaymentItemResponse.builder()
//                        .id(p.getId())
//                        .amount(p.getAmount())
//                        .paymentType(p.getPaymentType())
//                        .method(p.getMethod())
//                        .status(p.getStatus())
//                        .createdAt(p.getCreatedAt() != null ? p.getCreatedAt().toString() : null)
//                        .build()
//                )
//                .toList();
//
//        // 🔥 9. Return
//        return PaymentSummaryResponse.builder()
//                .bookingId(bookingId)
//                .totalPrice(totalPrice)
//                .depositPaid(deposit)
//                .finalPaid(finalPaid)
//                .penalty(penalty)
//                .extend(extend)
//                .totalPaid(totalPaid)
//                .remainingAmount(remaining)
//                .payments(paymentDTOs)
//                .build();
//    }
//
//    @Transactional
//    public List<PaymentItemResponse> getPaymentsByBooking(Long bookingId) {
//
//        Booking booking = bookingRepository.findById(bookingId)
//                .orElseThrow(() -> new RuntimeException("Booking không tồn tại"));
//
//        if (!"CHECKED_OUT".equals(booking.getStatus())) {
//            throw new RuntimeException("Booking chưa checkout");
//        }
//
//        return paymentRepository.findByBookingId(bookingId)
//                .stream()
//                .map(p -> PaymentItemResponse.builder()
//                        .id(p.getId())
//                        .amount(p.getAmount())
//                        .paymentType(p.getPaymentType())
//                        .method(p.getMethod())
//                        .status(p.getStatus())
//                        .createdAt(p.getCreatedAt() != null ? p.getCreatedAt().toString() : null)
//                        .build()
//                )
//                .toList();
//    }
//
//    @Transactional
//    public String paySinglePayment(PayRequest request) {
//
//        // 🔥 1. Validate method
//        if (!List.of("CASH", "VNPAY").contains(request.getMethod())) {
//            throw new RuntimeException("Method không hợp lệ");
//        }
//
//        // 🔥 2. Lấy booking
//        Booking booking = bookingRepository.findById(request.getBookingId())
//                .orElseThrow(() -> new RuntimeException("Booking không tồn tại"));
//
//        if (!"CHECKED_OUT".equals(booking.getStatus()) && "FINAL".equals(request.getMethod())) {
//            throw new RuntimeException("Booking chưa checkout. không thể thanh toán FINAL");
//        }
//
//        // 🔥 3. Lấy payment theo id
//        Payment payment = paymentRepository.findById(request.getPaymentId())
//                .orElseThrow(() -> new RuntimeException("Payment không tồn tại"));
//
//        // ❗ check payment có thuộc booking không
//        if (!payment.getBookingId().equals(request.getBookingId())) {
//            throw new RuntimeException("Payment không thuộc booking này");
//        }
//
//        // ❗ không cho update lại nếu đã PAID
//        if ("PAID".equals(payment.getStatus())) {
//            throw new RuntimeException("Payment đã thanh toán rồi");
//        }
//
//        // 🔥 4. Update
//        payment.setStatus("PAID");
//        payment.setPaidAt(LocalDateTime.now());
//        payment.setMethod(request.getMethod());
//        payment.setPaidAt(LocalDateTime.now());
//
//        paymentRepository.save(payment);
//
//        // 🔥 5. Check nếu đã thanh toán đủ → FINISHED
//        PaymentSummaryResponse summary = calculatePayment(request.getBookingId());
//
//        if (summary.getRemainingAmount().compareTo(BigDecimal.ZERO) == 0 && "FINAL".equals(request.getMethod())) {
//            booking.setStatus("FINISHED");
//            bookingRepository.save(booking);
//        }
//
//        return "Thanh toán thành công paymentId = " + payment.getId();
//    }
//
//    @Transactional
//    public RevenueDashboardResponse getTodayRevenue(Long typeIdInput) {
//
//        // ===== 1. Lấy RoomType =====
//
//        Long typeId = typeIdInput != null
//                ? typeIdInput
//                : roomTypeRepository.findAll()
//                .stream()
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("Không có RoomType"))
//                .getId();
//
//        RoomType roomType = roomTypeRepository.findById(typeId)
//                .orElseThrow(() -> new RuntimeException("RoomType không tồn tại"));
//
//        // ===== 2. Lấy room theo type =====
//
//        List<Room> rooms = roomRepository.findByTypeId(typeId);
//
//        Set<Long> roomIds = rooms.stream()
//                .map(Room::getId)
//                .collect(Collectors.toSet());
//
//        if (roomIds.isEmpty()) {
//            return emptyResponse(roomType);
//        }
//
//        // ===== 3. RoomKey -> Booking =====
//
//        List<RoomKey> roomKeys =
//                roomKeyRepository.findByRoomIdIn(roomIds);
//
//        Set<Long> bookingIds = roomKeys.stream()
//                .map(RoomKey::getBookingId)
//                .collect(Collectors.toSet());
//
//        if (bookingIds.isEmpty()) {
//            return emptyResponse(roomType);
//        }
//
//        // ===== 4. Booking =====
//
//        List<Booking> bookings =
//                bookingRepository.findByIdIn(bookingIds);
//
//        LocalDate today = LocalDate.now();
//
//        // ===== 5. Expected Revenue =====
//        // Booking tạo hôm nay
//
//        List<Booking> todayBookings = bookings.stream()
//                .filter(b ->
//                        b.getCreatedAt() != null &&
//                                b.getCreatedAt().toLocalDate().equals(today)
//                )
//                .toList();
//
//        BigDecimal expectedRevenue = todayBookings.stream()
//                .map(b -> safe(b.getTotalPrice()))
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        // ===== 6. Payment hôm nay =====
//
//        LocalDateTime start = today.atStartOfDay();
//        LocalDateTime end = today.plusDays(1).atStartOfDay();
//
//        List<Payment> todayPayments =
//                paymentRepository.findByCreatedAtBetween(start, end);
//
//        // DEBUG
//        System.out.println("TODAY PAYMENTS = " + todayPayments.size());
//
//        todayPayments.forEach(p -> {
//            System.out.println(
//                    "ID = " + p.getId()
//                            + " | amount = " + p.getAmount()
//                            + " | status = " + p.getStatus()
//                            + " | createdAt = " + p.getCreatedAt()
//            );
//        });
//
//        // ===== 7. Actual Revenue =====
//
//        BigDecimal actualRevenue = todayPayments.stream()
//                .filter(p ->
//                        p.getStatus() != null &&
//                                p.getStatus().trim().equalsIgnoreCase("PAID")
//                )
//                .map(p -> safe(p.getAmount()))
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        // ===== 8. Group =====
//
//        Map<String, BigDecimal> revenueByMethod = new HashMap<>();
//        Map<String, BigDecimal> revenueByStatus = new HashMap<>();
//
//        for (Payment p : todayPayments) {
//
//            BigDecimal amount = safe(p.getAmount());
//
//            // METHOD
//            String method = p.getMethod() != null
//                    ? p.getMethod()
//                    : "UNKNOWN";
//
//            revenueByMethod.merge(
//                    method,
//                    amount,
//                    BigDecimal::add
//            );
//
//            // STATUS
//            String status = p.getStatus() != null
//                    ? p.getStatus()
//                    : "UNKNOWN";
//
//            revenueByStatus.merge(
//                    status,
//                    amount,
//                    BigDecimal::add
//            );
//        }
//
//        // ===== 9. Response =====
//
//        return RevenueDashboardResponse.builder()
//                .typeId(typeId)
//                .typeName(roomType.getName())
//                .expectedRevenue(expectedRevenue)
//                .actualRevenue(actualRevenue)
//                .revenueByMethod(revenueByMethod)
//                .revenueByStatus(revenueByStatus)
//                .build();
//    }
//
//    // 🔥 helper
//    private BigDecimal safe(BigDecimal value) {
//        return value != null ? value : BigDecimal.ZERO;
//    }
//
//    private RevenueDashboardResponse emptyResponse(RoomType roomType) {
//        return RevenueDashboardResponse.builder()
//                .typeId(roomType.getId())
//                .typeName(roomType.getName())
//                .expectedRevenue(BigDecimal.ZERO)
//                .actualRevenue(BigDecimal.ZERO)
//                .revenueByMethod(new HashMap<>())
//                .revenueByStatus(new HashMap<>())
//                .build();
//    }
//
//    public Page<Payment> getPayments(
//            Integer bookingId,
//            String method,
//            String status,
//            LocalDate date,
//            int page,
//            int size
//    ) {
//
//        Pageable pageable = PageRequest.of(
//                page,
//                size,
//                Sort.by("created_at").descending()
//        );
//
//        return paymentRepository.searchPayments(
//                bookingId, method, status, date, pageable
//        );
//    }
//
//    public List<RevenueAdminDTO> getStatisticByType(
//            String method,
//            String status,
//            LocalDate date
//    ) {
//
//        return paymentRepository.statisticByPaymentType(method, status, date)
//                .stream()
//                .map(r -> new RevenueAdminDTO(
//                        (String) r[0],
//                        r[1] == null ? 0 : ((Number) r[1]).doubleValue()
//                ))
//                .toList();
//    }
//}