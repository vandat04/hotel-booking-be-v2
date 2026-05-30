package hotel_booking.service;

import hotel_booking.repository.BookingRepository;
import hotel_booking.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import hotel_booking.dto.request.*;
import hotel_booking.dto.response.*;
import hotel_booking.entity.*;
import hotel_booking.repository.*;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReceptionBookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final RoomScheduleRepository roomScheduleRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final RoomKeyRepository roomKeyRepository;
    private final RoomDamageRepository roomDamageRepository;
    private final CleanerNotificationRepository cleanerNotificationRepository;

    // ===================== 1. CHECK ONLY =====================
    public CheckAvailabilityResponse checkAvailability(CheckAvailabilityRequest req) {
        return bookingService.checkAvailability(req);
    }

    // ===================== 2. WALK-IN BOOKING =====================
    public WalkInBookingResponse createWalkInBooking(WalkInBookingRequest req) {

        // 1. CHECK AVAILABILITY FIRST
        CheckAvailabilityResponse availability = checkAvailability(req.getAvailabilityRequest());

        if (availability == null || !Boolean.TRUE.equals(availability.getAvailable())) {
            throw new RuntimeException("Rooms are not available for booking");
        }

        List<Integer> roomIds = availability.getListRoomCanBook();

        if (roomIds == null || roomIds.size() < req.getAvailabilityRequest().getNumberOfRoom()) {
            throw new RuntimeException("Not enough rooms available");
        }

        // 2. ROOM TYPE
        RoomType roomType = roomTypeRepository.findById(
                req.getAvailabilityRequest().getRoomTypeId()
        ).orElseThrow(() -> new RuntimeException("Room type not found"));

        // 4. CREATE BOOKING
        Booking booking = Booking.builder()
                .customer(null)
                .customerName(req.getCustomerName())
                .customerPhone(req.getCustomerPhone())
                .customerEmail(req.getCustomerEmail())

                .roomType(roomType)
                .requestedQuantity(req.getAvailabilityRequest().getNumberOfRoom())
                .requestedCheckin(req.getAvailabilityRequest().getCheckIn())
                .requestedCheckout(req.getAvailabilityRequest().getCheckOut())
                .bookingType(req.getAvailabilityRequest().getBookingType())

                .bookingSource("WALK-IN")
                .status("PENDING")
                .paymentStatus("UNPAID")

                .totalAmount(availability.getTotalAmount())
                .notes(req.getNotes())

                .createdAt(LocalDateTime.now())
                .build();

        booking = bookingRepository.save(booking);

        // 5. CREATE ROOM SCHEDULE
        Booking finalBooking = booking;

        List<RoomSchedule> schedules = roomIds.stream()
                .map(roomId -> RoomSchedule.builder()
                        .booking(finalBooking)
                        .room(roomRepository.getReferenceById(roomId))
                        .startAt(req.getAvailabilityRequest().getCheckIn())
                        .endAt(req.getAvailabilityRequest().getCheckOut())
                        .status("HOLD")
                        .createdAt(LocalDateTime.now())
                        .build()
                )
                .toList();

        roomScheduleRepository.saveAll(schedules);

        // 6. NOTIFY
        if (req.getCustomerEmail() != null && !req.getCustomerEmail().isBlank()) {
            emailService.sendCustomerEmail(
                    req.getCustomerEmail(),
                    "WALK-IN BOOKING CONFIRMED",
                    "Your booking has been confirmed at reception."
            );
        }

        notificationService.createCustomerNotification(
                null,
                booking,
                "WALK-IN BOOKING",
                "Booking confirmed at reception.",
                "BOOKING_SUCCESS"
        );

        // 7. RESPONSE
        return WalkInBookingResponse.builder()
                .bookingId(booking.getId())
                .status(booking.getStatus())
                .message("Walk-in booking created successfully")
                .numberOfRooms(roomIds.size())
                .totalAmount(availability.getTotalAmount())
                .build();
    }

    // ===================== 3. GET ALL BOOKINGS (Newest first) =====================
    public PageResponse<AdminBookingResponse> getAllBookings(PaginationRequest req) {
        // Mặc định luôn ưu tiên sắp xếp theo thời gian tạo mới nhất (createdAt giảm dần)
        if (req.getSortBy() == null || req.getSortBy().isBlank()) {
            req.setSortBy("createdAt");
        }
        if (req.getDirection() == null || req.getDirection().isBlank()) {
            req.setDirection("desc");
        }
        return bookingService.getAllBookings(req);
    }

    // ===================== 4. GET BOOKING DETAIL =====================
    public AdminBookingDetailResponse getBookingDetail(Integer bookingId) {
        return bookingService.getAdminBookingDetail(bookingId);
    }

    // ===================== 5. SEARCH BOOKINGS =====================
    public PageResponse<AdminBookingResponse> searchBookings(
            SearchBookingRequest request,
            PaginationRequest pagination
    ) {
        return bookingService.searchBookings(request, pagination);
    }

    // ===================== 6. CANCEL BOOKING =====================
    public void cancelBooking(Integer bookingId, CancelBookingRequest request) {
        bookingService.cancelBooking(bookingId, request);
    }

    // ===================== 7. REFUND BOOKING =====================
    public void refundBooking(Integer bookingId) {
        // Truyền customerId = null để báo cho hệ thống biết đây là quyền Admin/Receptionist
        bookingService.refundBooking(null, bookingId);
    }

    // ===================== 8. PAYMENT BOOKING =====================
    @Transactional
    public String createPaymentBookingByReceptionist(
            ReceptionistPaymentRequest request
    ) {

        // ===== FIND BOOKING =====
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("BOOKING_NOT_FOUND"));

        // ===== VALIDATE BOOKING =====
        paymentService.validateBookingPayment(booking);

        // ===== CHECK ALREADY PAID =====
        boolean alreadyPaid = paymentRepository
                .findByBooking_Id(booking.getId())
                .stream()
                .anyMatch(p -> "SUCCESS".equalsIgnoreCase(p.getStatus()));

        if (alreadyPaid) {
            throw new RuntimeException("BOOKING_ALREADY_PAID");
        }

        // ===== CREATE NEW PAYMENT =====
        String txnRef = String.valueOf(System.currentTimeMillis());
        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getTotalAmount())
                .paymentMethod(request.getPaymentMethod())
                .gatewayName(request.getPaymentMethod().equals("CASH") ? "CASH" : "VNPAY")
                .paymentType("FULL_ROOM_CHARGE")
                .status("SUCCESS")
                .transactionReference(txnRef)
                .paymentDate(LocalDateTime.now())
                .notes(request.getNotes())
                .build();
        paymentRepository.save(payment);

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

        // ===== SEND NOTIFICATION & MAIL =====
        User user = null;
        if (booking.getCustomer() != null) {
            user = userRepository.findById(booking.getCustomer().getId())
                    .orElse(null);
        }
        String email = booking.getCustomerEmail();

        // ================= SEND EMAIL =================
        System.out.println("===email==== "+email);
        if (email != null && !email.isBlank()) {
            emailService.sendCustomerEmail(email, "BOOKING ROOM IN CHECK-X", "Payment Success.");
        }
        notificationService.createCustomerNotification( user, booking, "BOOKING ROOM IN CHECK-X", "Payment Success.", "PAYMENT_SUCCESS");

        return "success";
    }

    // ===================== 9. CHECK_IN =====================
    @Transactional
    public String checkInBooking(Integer bookingId) {

        // ================= FIND BOOKING =================
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("BOOKING_NOT_FOUND"));

        // ================= VALIDATE STATUS =================
        if (!"CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("BOOKING_NOT_CONFIRMED");
        }

        if (!"PAID".equalsIgnoreCase(booking.getPaymentStatus())) {
            throw new RuntimeException("BOOKING_NOT_PAID");
        }

        // ================= TIME VALIDATION =================
        LocalDateTime now = LocalDateTime.now();

        if (booking.getRequestedCheckin() != null && now.isBefore(booking.getRequestedCheckin())) {
            throw new RuntimeException("TOO_EARLY_CHECKIN");
        }

        // ================= UPDATE BOOKING =================
        booking.setStatus("CHECKED_IN");
        booking.setUpdatedAt(now);
        bookingRepository.save(booking);

        // ================= GET ROOM SCHEDULES =================
        List<RoomSchedule> schedules = roomScheduleRepository.findByBooking_Id(booking.getId());

        if (schedules.isEmpty()) {
            throw new RuntimeException("ROOM_SCHEDULE_NOT_FOUND");
        }

        List<RoomKey> keys = new ArrayList<>();

        for (RoomSchedule rs : schedules) {

            // ================= UPDATE SCHEDULE =================
            if ("SCHEDULED".equalsIgnoreCase(rs.getStatus()) || "HOLD".equalsIgnoreCase(rs.getStatus())) {
                rs.setStatus("ACTIVE");
                rs.setUpdatedAt(now);
            }

            // ================= CREATE ROOM KEY =================
            RoomKey key = RoomKey.builder()
                    .roomSchedule(rs)
                    .codeNumber(generateRoomKeyCode())
                    .qrCodeData(UUID.randomUUID().toString())
                    .activatedAt(booking.getRequestedCheckin())
                    .expiredAt(booking.getRequestedCheckout())
                    .status("ACTIVE")
                    .build();

            keys.add(key);
        }

        roomScheduleRepository.saveAll(schedules);
        roomKeyRepository.saveAll(keys);

        return "CHECK-IN SUCCESS";
    }

    private String generateRoomKeyCode() {
        return String.valueOf(10000000 + new Random().nextInt(90000000));
    }

    // ===================== 10. CHECK_OUT =====================
    @Transactional
    public String checkOutBooking(Integer bookingId) {

        // ===== FIND BOOKING =====
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("BOOKING_NOT_FOUND"));

        // ===== VALIDATE STATUS =====
        if (!"CHECKED_IN".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("BOOKING_NOT_CHECKED_IN");
        }

        // ===== UPDATE BOOKING =====
        booking.setStatus("CHECKED_DAMAGE_ROOM");
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        // ===== UPDATE ROOM SCHEDULES =====
        List<RoomSchedule> schedules = roomScheduleRepository.findByBooking_Id(bookingId);

        for (RoomSchedule rs : schedules) {
            // 1. kết thúc lịch phòng
            rs.setStatus("COMPLETED");
            rs.setUpdatedAt(LocalDateTime.now());
            // 2. đánh dấu phòng cần dọn
            Room room = rs.getRoom();
            // chỉ chuyển DIRTY nếu đang READY (tránh overwrite MAINTENANCE)
            if ("READY".equals(room.getStatus()) || room.getStatus() == null) {
                room.setStatus("DIRTY");
            }
            room.setUpdatedAt(LocalDateTime.now());
        }
        roomScheduleRepository.saveAll(schedules);

        // ===== UPDATE ROOM KEYS =====
        List<RoomKey> roomKeys = roomKeyRepository.findByRoomSchedule_Booking_Id(bookingId);

        for (RoomKey key : roomKeys) {
            key.setStatus("EXPIRED");
            key.setUpdatedAt(LocalDateTime.now());
        }
        roomKeyRepository.saveAll(roomKeys);

        // ===== CREATE CLEANER NOTIFICATION =====
        String roomNumber = schedules.isEmpty() ? "N/A" : schedules.get(0).getRoom().getRoomNumber();
        CleanerNotification cleanerNoti = CleanerNotification.builder()
                .booking(booking)
                .title("Yêu cầu kiểm kê phòng " + roomNumber)
                .message("Phòng " + roomNumber + " đã checkout. Vui lòng dọn dẹp và kiểm kê vật dụng phòng.")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        cleanerNotificationRepository.save(cleanerNoti);

        return "CHECK_OUT_SUCCESS";
    }

    // ===================== 13. GET BOOKING DAMAGES =====================
    public BookingDamageResponse getBookingDamages(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("BOOKING_NOT_FOUND"));

        List<RoomSchedule> schedules = roomScheduleRepository.findByBooking_Id(bookingId);
        String roomNumber = schedules.isEmpty() ? "N/A" : schedules.get(0).getRoom().getRoomNumber();

        List<RoomDamage> damages = roomDamageRepository.findByBookingId(bookingId);
        
        java.math.BigDecimal totalDamageFee = java.math.BigDecimal.ZERO;
        List<BookingDamageResponse.DamageItemDetail> details = new ArrayList<>();
        
        for (RoomDamage d : damages) {
            totalDamageFee = totalDamageFee.add(d.getActualDamageFee());
            details.add(BookingDamageResponse.DamageItemDetail.builder()
                    .damageId(d.getId())
                    .itemId(d.getItem().getId())
                    .itemName(d.getItem().getItemName())
                    .quantity(d.getQuantity())
                    .actualDamageFee(d.getActualDamageFee())
                    .note(d.getNote())
                    .evidenceImageUrl(d.getEvidenceImageUrl())
                    .reportedAt(d.getReportedAt())
                    .build());
        }

        return BookingDamageResponse.builder()
                .bookingId(booking.getId())
                .customerName(booking.getCustomerName())
                .customerPhone(booking.getCustomerPhone())
                .roomNumber(roomNumber)
                .totalDamageFee(totalDamageFee)
                .bookingStatus(booking.getStatus())
                .damages(details)
                .build();
    }

    // ===================== 14. PAY BOOKING DAMAGES =====================
    @Transactional
    public String payBookingDamages(Integer bookingId, PayDamageRequest request) {
        // 1. Validate Booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("BOOKING_NOT_FOUND"));

        if (!"CHECKED_DAMAGE_ROOM".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("INVALID_BOOKING_STATUS_FOR_DAMAGE_PAYMENT");
        }

        // 2. Lấy danh sách hư hại để tính tổng tiền
        List<RoomDamage> damages = roomDamageRepository.findByBookingId(bookingId);
        if (damages.isEmpty()) {
            throw new RuntimeException("NO_DAMAGES_REPORTED_FOR_THIS_BOOKING");
        }

        java.math.BigDecimal totalDamageFee = java.math.BigDecimal.ZERO;
        for (RoomDamage d : damages) {
            totalDamageFee = totalDamageFee.add(d.getActualDamageFee());
        }

        if (totalDamageFee.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("TOTAL_DAMAGE_FEE_MUST_BE_GREATER_THAN_ZERO");
        }

        // 3. Tạo bản ghi Payment
        String txnRef = "DMG" + System.currentTimeMillis();
        Payment payment = Payment.builder()
                .booking(booking)
                .amount(totalDamageFee)
                .paymentMethod(request.getPaymentMethod())
                .gatewayName("CASH".equalsIgnoreCase(request.getPaymentMethod()) ? "CASH" : "VNPAY")
                .paymentType("DAMAGE_CHARGE")
                .status("SUCCESS")
                .transactionReference(txnRef)
                .paymentDate(LocalDateTime.now())
                .notes(request.getNotes())
                .build();
        paymentRepository.save(payment);

        // 4. Tạo bản ghi Invoice
        Invoice invoice = Invoice.builder()
                .booking(booking)
                .payment(payment)
                .customerName(booking.getCustomerName())
                .customerEmail(booking.getCustomerEmail())
                .customerPhone(booking.getCustomerPhone())
                .amountPaid(totalDamageFee)
                .invoiceDescription("BỒI THƯỜNG VẬT DỤNG")
                .issuedAt(LocalDateTime.now())
                .isSentEmail(false)
                .build();
        invoiceRepository.save(invoice);

        // 5. Cập nhật Booking trạng thái sang CHECKED_OUT
        booking.setStatus("CHECKED_OUT");
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        // Tự động cập nhật RoomSchedules thành COMPLETED và Rooms thành READY khi thanh toán xong phí hư hại
        List<RoomSchedule> schedules = roomScheduleRepository.findByBooking_Id(bookingId);
        LocalDateTime now = LocalDateTime.now();
        for (RoomSchedule rs : schedules) {
            rs.setStatus("COMPLETED");
            rs.setUpdatedAt(now);

            Room room = rs.getRoom();
            if (!"MAINTENANCE".equals(room.getStatus())) {
                room.setStatus("READY");
            }
            room.setUpdatedAt(now);
            roomRepository.save(room);
        }
        roomScheduleRepository.saveAll(schedules);

        // 6. Gửi Email / Thông báo nếu có
        User user = null;
        if (booking.getCustomer() != null) {
            user = userRepository.findById(booking.getCustomer().getId()).orElse(null);
        }
        notificationService.createCustomerNotification(
                user,
                booking,
                "BỒI THƯỜNG VẬT DỤNG THÀNH CÔNG",
                "Phí bồi thường vật dụng đã thanh toán thành công. Check-out hoàn tất.",
                "PAYMENT_SUCCESS"
        );

        return "PAYMENT_DAMAGE_SUCCESS";
    }

    // VIEW

    @Transactional
    public String requestRoomClean(Integer roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("ROOM_NOT_FOUND"));

        // Tìm lịch phòng đang active của phòng này
        List<RoomSchedule> activeSchedules = roomScheduleRepository.findActiveSchedulesByRoomId(roomId);

        Booking booking = null;
        if (!activeSchedules.isEmpty()) {
            booking = activeSchedules.get(0).getBooking();
        }

        CleanerNotification cleanerNoti = CleanerNotification.builder()
                .booking(booking)
                .title("Yêu cầu dọn dẹp phòng " + room.getRoomNumber())
                .message("Lễ tân yêu cầu kiểm tra và dọn dẹp đột xuất phòng " + room.getRoomNumber() + ".")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        cleanerNotificationRepository.save(cleanerNoti);

        return "SUCCESS";
    }

}
