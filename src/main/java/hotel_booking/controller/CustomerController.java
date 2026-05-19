package hotel_booking.controller;

import hotel_booking.dto.request.*;
import hotel_booking.dto.response.BookingDetailResponse;
import hotel_booking.dto.response.BookingHistoryResponse;
import hotel_booking.dto.response.CustomerNotificationResponse;
import hotel_booking.dto.response.UserProfileResponse;
import hotel_booking.service.*;
import lombok.RequiredArgsConstructor;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.attribute.UserPrincipal;
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

    // ================= GET USER ID =================
    public Integer getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Integer.parseInt(authentication.getName());
    }

    // ================= VIEW PROFILE =================
    @GetMapping("/me")
    public UserProfileResponse getMyProfile() {
        return userService.getMyProfile(getUserId());
    }

    // ================= UPDATE PROFILE =================
    @PutMapping("/me")
    public UserProfileResponse updateProfile(
            @RequestBody UpdateProfileRequest request
    ) {
        return userService.updateProfile(getUserId(), request);
    }

    // ================= UPDATE AVATAR PROFILE =================
    @PutMapping("/me/avatar")
    public UserProfileResponse updateAvatar(
            @RequestParam("file") MultipartFile file
    ) {
        return userService.updateAvatar(getUserId(), file);
    }

    // ================= CHANGE PASSWORD =================
    @PutMapping("/me/change-password")
    public String changePassword(
            @RequestBody ChangePasswordRequest request
    ) {
        return userService.changePassword(getUserId(), request);
    }

    // ================= VIEW HISTORY BOOKING =================
    @GetMapping("bookings")
    public Page<BookingHistoryResponse> getBookingHistory(
            PaginationRequest request
    ) {
        Integer userId = getUserId();
        return bookingService.getBookingHistory(userId, request);
    }

    // ================= VIEW HISTORY BOOKING BY STATUS =================
    @GetMapping("booking")
    public Page<BookingHistoryResponse> getBookingHistory(
            PaginationRequest request,
            @RequestParam("status") String status
    ) {
        Integer userId = getUserId();
        return bookingService.getBookingHistory(userId, request, status);
    }

    // ================= VIEW HISTORY BOOKING DETAILS=================
    @GetMapping("/booking/{bookingId}")
    public BookingDetailResponse getBookingDetail(
            @PathVariable Integer bookingId
    ) {
        System.out.println(bookingId);
        return bookingService.getBookingDetail(getUserId(), bookingId);
    }

    // ================= CANCEL BOOKING =================
    @PutMapping("/booking/{bookingId}/cancel")
    public ResponseEntity<String> cancelBooking(
            @PathVariable Integer bookingId
    ) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok("Booking cancelled successfully");
    }

    // ================= PAYMENT BOOKING =================
    @PostMapping("/booking/payment/vnpay")
    public ResponseEntity<?> payment(
            @RequestBody PaymentRequest request
    ) {
        String paymentUrl =  paymentService.createVnPayPayment( getUserId(), request  );
        return ResponseEntity.ok(
                Map.of(
                        "message", "Create payment success",
                        "paymentUrl", paymentUrl
                )
        );
    }
    // ================= PAYMENT BOOKING RETURN =================
    @GetMapping("/booking/payment/vnpay-return")
    public ResponseEntity<?> vnpayReturn(
            @RequestParam Map<String, String> paymentUrl
    ) {
        paymentService.handleVnPayReturn(paymentUrl);
        return ResponseEntity.ok(
                Map.of(
                        "message", "Payment success"
                )
        );
    }

    // ================= REFUND BOOKING =================
    @PutMapping("/booking/{bookingId}/refund")
    public ResponseEntity<?> refundBooking(
            @PathVariable Integer bookingId
    ) {
        bookingService.refundBooking(getUserId(), bookingId);
        return ResponseEntity.ok(
                Map.of(
                        "message", "Refund success"
                )
        );
    }
    // ================= REVIEW =================
    @PostMapping("/booking/review")
    public ResponseEntity<String> createReview(
            @RequestBody CreateReviewRequest request
    ) throws BadRequestException {
        reviewService.createReview(getUserId(), request);
        return ResponseEntity.ok("Review submitted successfully");
    }

    // ================= VIEW NOTIFICATION ================
    @GetMapping("/notification")
    public Page<CustomerNotificationResponse> getNotifications(
            @ModelAttribute PaginationRequest request
    ) {
        return notificationService.getCustomerNotifications(getUserId(), request);
    }

    // ================= EXTEND BOOKING =================

}