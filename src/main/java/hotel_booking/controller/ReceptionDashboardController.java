package hotel_booking.controller;

import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.response.ApiResponse;
import hotel_booking.dto.response.CustomerNotificationResponse;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.dto.response.ReceptionDashboardResponse;
import hotel_booking.dto.response.WeeklyOccupancyDTO;
import hotel_booking.service.NotificationService;
import hotel_booking.service.OccupancyAnalyticsService;
import hotel_booking.service.ReceptionDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/receptionist/dashboard")
@RequiredArgsConstructor
public class ReceptionDashboardController {
    private final ReceptionDashboardService dashboardService;
    private final OccupancyAnalyticsService occupancyAnalyticsService;
    private final NotificationService notificationService;

    // DASHBOARD =========================================
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<ReceptionDashboardResponse>> getTodayDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getTodayDashboard()));
    }

    // CONG SUAT PHONG O/TUAN =========================================
    @GetMapping("/room-weekly-active")
    public ResponseEntity<ApiResponse<List<WeeklyOccupancyDTO>>> getWeeklyActive() {
        return ResponseEntity.ok(ApiResponse.success(occupancyAnalyticsService.getWeeklyActiveOccupancy()));
    }

    // VIEW LIST NOTIFICATION =========================================
    @GetMapping("/customer-notification")
    public ResponseEntity<ApiResponse<PageResponse<CustomerNotificationResponse>>> getAll(
            PaginationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getAll(request)));
    }

}

