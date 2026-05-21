package hotel_booking.controller;

import hotel_booking.dto.request.AttendanceFilterRequest;
import hotel_booking.dto.response.ApiResponse;
import hotel_booking.dto.response.AttendanceResponse;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/attendance")
@RequiredArgsConstructor
public class AdminAttendanceController {

    private final AttendanceService attendanceService;

    // GET ATTENDANCE LIST =============================
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AttendanceResponse>>> getAttendanceList(
            AttendanceFilterRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getAttendanceList(request)));
    }
}
