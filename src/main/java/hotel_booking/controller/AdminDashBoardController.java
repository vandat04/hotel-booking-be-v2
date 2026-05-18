package hotel_booking.controller;

import hotel_booking.dto.request.BookingDashboardRequest;
import hotel_booking.dto.response.*;
import hotel_booking.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashBoardController {

    private final RoomTypeService roomTypeService;
    private final ReviewService reviewService;
    private final BookingService bookingService;
    private final OTAChannelService otaChannelService;
    private final PaymentService paymentService;

    // ================= STATISTIC RATE ROOM TYPE =================
    @GetMapping("/room-type-statistic")
    public ResponseEntity<List<RoomTypeBookingStatsResponse>> getBookingStats(
            @RequestParam Integer year,
            @RequestParam(required = false) Integer month
    ) {
        BookingDashboardRequest request = new BookingDashboardRequest();
        request.setYear(year);
        request.setMonth(month);

        return ResponseEntity.ok(
                roomTypeService.getBookingStats(request)
        );
    }

    // ================= STATISTIC RATE REVIEW  =================
    @GetMapping("/review-rate-statistic")
    public ReviewStatisticsResponse getReviewStatistics() {
        return reviewService.getReviewStatistics();
    }

    // ================= BOOKING DASHBOARD =================
    @GetMapping("/booking-statistic")
    public BookingStatisticsResponse getBookingStatistics() {
        return bookingService.getBookingStatistics();
    }

    // ================= OTA DASHBOARD =================
    @GetMapping("/ota-statistic")
    public OTADashboardResponse getOTADashboard() {
        return otaChannelService.getDashboard();
    }

    // ================= PAYMENT DASHBOARD =================
    @GetMapping("/payment-dashboard")
    public PaymentDashboardResponse getPaymentDashboard() {
        return paymentService.getPaymentDashboard();
    }

    // ================= REVENUE DASHBOARD =================
    @GetMapping("/revenue-dashboard")
    public RevenueStatisticsResponse getRevenueStatistics() {
        return paymentService.getRevenueStatistics();
    }
}