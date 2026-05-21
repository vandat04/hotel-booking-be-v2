package hotel_booking.controller;

import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.response.*;
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
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile() {
        return ResponseEntity.ok(ApiResponse.success(userService.getMyProfile(getUserId())));
    }

    // ================= VIEW DANH SACH CA LAM =================
    @GetMapping("/my-current-week")
    public ResponseEntity<ApiResponse<List<AssignStaffResponse>>> getMyCurrentWeekShifts() {
        return ResponseEntity.ok(ApiResponse.success(staffShiftService.getMyCurrentWeekShifts(getUserId())));
    }

    // ================= CHECK-IN =================
    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<AttendanceResponse>> checkIn() {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.checkIn(getUserId())));
    }

    // ================= CHECK-OUT =================
    @PostMapping("/check-out/{assignmentId}")
    public ResponseEntity<ApiResponse<String>> checkOut(
            @PathVariable Integer assignmentId
    ) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.checkOut(assignmentId, getUserId())));
    }

    // ==================== VIEW LIST ATTENDANCE ==============
    @GetMapping("/my-attendance")
    public ResponseEntity<ApiResponse<PageResponse<AttendanceResponse>>> getMyAttendance(
            PaginationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getMyAttendance(getUserId(), request)));
    }

    // ==================== VIEW SALARY  ==============
    @GetMapping("/salary/current-month")
    public ResponseEntity<ApiResponse<SalarySheetResponse>> viewCurrentMonthSalary() {
        return ResponseEntity.ok(ApiResponse.success(salaryService.viewCurrentMonthSalary(getUserId())));
    }

}

