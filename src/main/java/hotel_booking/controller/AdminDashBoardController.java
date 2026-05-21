package hotel_booking.controller;

import hotel_booking.dto.request.BookingDashboardRequest;
import hotel_booking.dto.response.*;
import hotel_booking.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashBoardController {

    private final RoomTypeService roomTypeService;
    private final ReviewService reviewService;
    private final BookingService bookingService;
    private final OTAChannelService otaChannelService;
    private final PaymentService paymentService;
    private final UserService userService;

    // ================= STATISTIC RATE ROOM TYPE =================
    @GetMapping("/room-type-statistic")
    public ResponseEntity<ApiResponse<List<RoomTypeBookingStatsResponse>>> getBookingStats(
            @RequestParam Integer year,
            @RequestParam(required = false) Integer month
    ) {
        BookingDashboardRequest request = new BookingDashboardRequest();
        request.setYear(year);
        request.setMonth(month);

        return ResponseEntity.ok(ApiResponse.success(
                roomTypeService.getBookingStats(request)
        ));
    }

    // ================= STATISTIC RATE REVIEW  =================
    @GetMapping("/review-rate-statistic")
    public ResponseEntity<ApiResponse<ReviewStatisticsResponse>> getReviewStatistics() {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getReviewStatistics()));
    }

    // ================= BOOKING DASHBOARD =================
    @GetMapping("/booking-statistic")
    public ResponseEntity<ApiResponse<BookingStatisticsResponse>> getBookingStatistics() {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getBookingStatistics()));
    }

    // ================= OTA DASHBOARD =================
    @GetMapping("/ota-statistic")
    public ResponseEntity<ApiResponse<OTADashboardResponse>> getOTADashboard() {
        return ResponseEntity.ok(ApiResponse.success(otaChannelService.getDashboard()));
    }

    // ================= PAYMENT DASHBOARD =================
    @GetMapping("/payment-statistic")
    public ResponseEntity<ApiResponse<PaymentDashboardResponse>> getPaymentDashboard() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentDashboard()));
    }

    // ================= REVENUE DASHBOARD =================
    @GetMapping("/revenue-statistic")
    public ResponseEntity<ApiResponse<RevenueStatisticsResponse>> getRevenueStatistics() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getRevenueStatistics()));
    }

    // ================ STAFF STATISTIC DASHBOARD ==========
    @GetMapping("/staff-statistic")
    public ResponseEntity<ApiResponse<StaffDashboardResponse>> getStaffDashboard() {
        return ResponseEntity.ok(ApiResponse.success(userService.getStaffDashboard()));
    }
}