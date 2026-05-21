package hotel_booking.controller;

import hotel_booking.dto.request.*;
import hotel_booking.dto.response.*;
import hotel_booking.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final UserService userService;
    private final BookingService bookingService;
    private final NotificationService notificationService;
    private final ReviewService reviewService;
    private final PaymentService paymentService;

    // ================= GET CURRENT USER ID =================
    private Integer getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Integer.parseInt(authentication.getName());
    }

    // ========================================================
    // ==================== PROFILE ===========================
    // ========================================================

    // GET /api/customer/me
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile() {
        return ResponseEntity.ok(ApiResponse.success(userService.getMyProfile(getUserId())));
    }

    // PUT /api/customer/me
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully",
                userService.updateProfile(getUserId(), request)));
    }

    // PUT /api/customer/me/avatar
    @PutMapping("/me/avatar")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateAvatar(
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(ApiResponse.success("Avatar updated successfully",
                userService.updateAvatar(getUserId(), file)));
    }

    // PUT /api/customer/me/change-password
    @PutMapping("/me/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.changePassword(getUserId(), request)));
    }

    // ========================================================
    // ==================== BOOKINGS ==========================
    // ========================================================

    // POST /api/customer/bookings → 201 CREATED
    // Create a new booking (previously in GuestController as /hotel/room-type/book-now)
    @PostMapping("/bookings")
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody CreateBookingRequest request
    ) {
        UserProfileResponse user = userService.getMyProfile(getUserId());

        if (user.getId() == null) {
            throw new hotel_booking.exception.AppException("Please login to create a booking");
        }
        if (user.getFullName() == null || user.getPhone() == null || user.getEmail() == null) {
            throw new hotel_booking.exception.AppException(
                    org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY,
                    "Please complete your profile (full name, phone, email) before booking"
            );
        }

        request.setCustomerId(user.getId());
        request.setCustomerName(user.getFullName());
        request.setCustomerPhone(user.getPhone());
        request.setCustomerEmail(user.getEmail());

        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Booking created successfully", response));
    }

    // GET /api/customer/bookings?status=CONFIRMED&page=0&size=10
    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<PageResponse<BookingHistoryResponse>>> getBookingHistory(
            PaginationRequest request,
            @RequestParam(value = "status", required = false) String status
    ) {
        Integer userId = getUserId();
        Page<BookingHistoryResponse> bookingPage;
        if (status == null || status.trim().isEmpty()) {
            bookingPage = bookingService.getBookingHistory(userId, request);
        } else {
            bookingPage = bookingService.getBookingHistory(userId, request, status);
        }

        PageResponse<BookingHistoryResponse> pageResponse = PageResponse.<BookingHistoryResponse>builder()
                .content(bookingPage.getContent())
                .page(bookingPage.getNumber())
                .size(bookingPage.getSize())
                .totalElements(bookingPage.getTotalElements())
                .totalPages(bookingPage.getTotalPages())
                .last(bookingPage.isLast())
                .build();

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    // GET /api/customer/bookings/{bookingId}
    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<ApiResponse<BookingDetailResponse>> getBookingDetail(
            @PathVariable Integer bookingId
    ) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getBookingDetail(getUserId(), bookingId)));
    }

    // PUT /api/customer/bookings/{bookingId}/cancel
    @PutMapping("/bookings/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelBooking(
            @PathVariable Integer bookingId
    ) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully"));
    }

    // PUT /api/customer/bookings/{bookingId}/refund
    @PutMapping("/bookings/{bookingId}/refund")
    public ResponseEntity<ApiResponse<String>> refundBooking(
            @PathVariable Integer bookingId
    ) {
        bookingService.refundBooking(getUserId(), bookingId);
        return ResponseEntity.ok(ApiResponse.success("Refund request submitted successfully"));
    }

    // ========================================================
    // ==================== PAYMENT ===========================
    // ========================================================

    // POST /api/customer/bookings/payment/vnpay
    @PostMapping("/bookings/payment/vnpay")
    public ResponseEntity<ApiResponse<Map<String, String>>> payment(
            @Valid @RequestBody PaymentRequest request
    ) {
        String paymentUrl = paymentService.createVnPayPayment(getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(
                "Payment URL created successfully",
                Map.of("paymentUrl", paymentUrl)
        ));
    }

    // GET /api/customer/bookings/payment/vnpay-return
    @GetMapping("/bookings/payment/vnpay-return")
    public ResponseEntity<ApiResponse<Map<String, String>>> vnpayReturn(
            @RequestParam Map<String, String> params
    ) {
        paymentService.handleVnPayReturn(params);
        return ResponseEntity.ok(ApiResponse.success(
                "Payment processed successfully",
                Map.of("message", "Payment success")
        ));
    }

    // ========================================================
    // ==================== REVIEWS ===========================
    // ========================================================

    // POST /api/customer/reviews → 201 CREATED
    @PostMapping("/reviews")
    public ResponseEntity<ApiResponse<String>> createReview(
            @Valid @RequestBody CreateReviewRequest request
    ) {
        reviewService.createReview(getUserId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review submitted successfully"));
    }

    // ========================================================
    // ==================== NOTIFICATIONS =====================
    // ========================================================

    // GET /api/customer/notifications?page=0&size=10
    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<PageResponse<CustomerNotificationResponse>>> getNotifications(
            @ModelAttribute PaginationRequest request
    ) {
        Page<CustomerNotificationResponse> notificationPage = notificationService.getCustomerNotifications(getUserId(), request);
        PageResponse<CustomerNotificationResponse> pageResponse = PageResponse.<CustomerNotificationResponse>builder()
                .content(notificationPage.getContent())
                .page(notificationPage.getNumber())
                .size(notificationPage.getSize())
                .totalElements(notificationPage.getTotalElements())
                .totalPages(notificationPage.getTotalPages())
                .last(notificationPage.isLast())
                .build();
        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }
}