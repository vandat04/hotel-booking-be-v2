package hotel_booking.controller;

import hotel_booking.dto.request.ChangePasswordRequest;
import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.request.UpdateProfileRequest;
import hotel_booking.dto.response.BookingHistoryResponse;
import hotel_booking.dto.response.UserProfileResponse;
import hotel_booking.service.BookingService;
import hotel_booking.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final UserService userService;
    private final BookingService bookingService;

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

    // ================= CANCEL BOOKING =================

    // ================= PAYMENT BOOKING =================

    // ================= REFUND BOOKING =================

    // ================= REVIEW =================

    // ================= EXTEND BOOKING =================

    // ================= VIEW NOTIFICATION =================



}