package hotel_booking.controller;

import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.response.AssignStaffResponse;
import hotel_booking.dto.response.AttendanceResponse;
import hotel_booking.dto.response.SalarySheetResponse;
import hotel_booking.dto.response.UserProfileResponse;
import hotel_booking.service.AttendanceService;
import hotel_booking.service.SalaryService;
import hotel_booking.service.StaffShiftService;
import hotel_booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final UserService userService;
    private final StaffShiftService staffShiftService;
    private final AttendanceService attendanceService;
    private final SalaryService salaryService;

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

    // ================= VIEW DANH SACH CA LAM =================
    @GetMapping("/my-current-week")
    public List<AssignStaffResponse> getMyCurrentWeekShifts(
    ) {
        return staffShiftService.getMyCurrentWeekShifts(getUserId());
    }

    // ================= CHECK-IN =================
    @PostMapping("/check-in")
    public AttendanceResponse checkIn(
    ) {
        return attendanceService.checkIn(getUserId());
    }

    // ================= CHECK-OUT =================
    @PostMapping("/check-out/{assignmentId}")
    public ResponseEntity<?> checkOut(
            @PathVariable Integer assignmentId
    ) {
        return ResponseEntity.ok(attendanceService.checkOut(assignmentId, getUserId()));
    }

    // ==================== VIEW LIST ATTENDANCE ==============
    @GetMapping("/my-attendance")
    public ResponseEntity<?> getMyAttendance(
            PaginationRequest request
    ) {
        return ResponseEntity.ok(
                attendanceService.getMyAttendance(getUserId(), request)
        );
    }

    // ==================== VIEW SALARY  ==============
    @GetMapping("/salary/current-month")
    public ResponseEntity<SalarySheetResponse> viewCurrentMonthSalary() {
        return ResponseEntity.ok(salaryService.viewCurrentMonthSalary(getUserId()));
    }

}
