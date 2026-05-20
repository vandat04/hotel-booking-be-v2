package hotel_booking.controller;

import hotel_booking.dto.request.AttendanceFilterRequest;
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
    public ResponseEntity<?> getAttendanceList(
            AttendanceFilterRequest request
    ) {
        return ResponseEntity.ok(attendanceService.getAttendanceList(request));
    }

    // GET ATTENDANCE LIST =============================
}
