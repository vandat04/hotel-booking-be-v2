package hotel_booking.controller;

import hotel_booking.dto.request.AdminStaffListRequest;
import hotel_booking.dto.request.CreateAccountStaffByAdmin;
import hotel_booking.dto.request.UpdateProfileByAdminRequest;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.dto.response.UserProfileResponse;
import hotel_booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/staff")
@RequiredArgsConstructor
public class AdminStaffController {

    private final UserService userService;

    //================= CREATE STAFF ACCOUNT =================
    @PostMapping("/add")
    public ResponseEntity<?> register(
            @RequestBody CreateAccountStaffByAdmin request
    ) {
        return ResponseEntity.ok(userService.createStaffAccount(request));
    }

    // ================= UPDATE PROFILE =================
    @PutMapping("/{staffId}")
    public UserProfileResponse updateProfileByAdmin(
            @PathVariable Integer staffId,
            @RequestBody UpdateProfileByAdminRequest request
    ) {
        return userService.updateProfileByAdmin(staffId, request);
    }

    // ================= VIEW STAFF LIST =================
    @GetMapping()
    public ResponseEntity<PageResponse<UserProfileResponse>> getStaffs(
            AdminStaffListRequest request
    ) {
        return ResponseEntity.ok(userService.getStaffs(request));
    }

    // ================= UPDATE PROFILE BY ADMIN =================
    @GetMapping("/{staffId}")
    public ResponseEntity<UserProfileResponse> getStaffDetail(
            @PathVariable Integer staffId
    ) {
        return ResponseEntity.ok(userService.getStaffDetail(staffId));
    }
}
