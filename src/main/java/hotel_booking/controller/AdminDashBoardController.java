package hotel_booking.controller;

import hotel_booking.dto.request.BookingDashboardRequest;
import hotel_booking.dto.response.ReviewStatisticsResponse;
import hotel_booking.dto.response.RoomTypeBookingStatsResponse;
import hotel_booking.service.ReviewService;
import hotel_booking.service.RoomTypeService;
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
}