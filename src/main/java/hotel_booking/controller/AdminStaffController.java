package hotel_booking.controller;

import hotel_booking.dto.request.AdminStaffListRequest;
import hotel_booking.dto.request.CreateAccountStaffByAdmin;
import hotel_booking.dto.request.UpdateProfileByAdminRequest;
import hotel_booking.dto.response.ApiResponse;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.dto.response.UserProfileResponse;
import hotel_booking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/staff")
@RequiredArgsConstructor
public class AdminStaffController {

    private final UserService userService;

    // ================= CREATE STAFF ACCOUNT =================
    // POST /api/admin/staff → 201 CREATED
    @PostMapping
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody CreateAccountStaffByAdmin request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Staff account created successfully", userService.createStaffAccount(request)));
    }

    // ================= UPDATE STAFF PROFILE =================
    // PUT /api/admin/staff/{staffId}
    @PutMapping("/{staffId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfileByAdmin(
            @PathVariable Integer staffId,
            @Valid @RequestBody UpdateProfileByAdminRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Staff profile updated successfully",
                userService.updateProfileByAdmin(staffId, request)));
    }

    // ================= VIEW STAFF LIST (paginated, filterable) =================
    // GET /api/admin/staff?role=CLEANER&page=0&size=10
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserProfileResponse>>> getStaffs(
            AdminStaffListRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.getStaffs(request)));
    }

    // ================= VIEW STAFF DETAIL =================
    // GET /api/admin/staff/{staffId}
    @GetMapping("/{staffId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getStaffDetail(
            @PathVariable Integer staffId
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.getStaffDetail(staffId)));
    }
}
