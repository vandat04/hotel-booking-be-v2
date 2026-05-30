package hotel_booking.controller;

import hotel_booking.dto.request.CreateRoleSalaryConfigRequest;
import hotel_booking.dto.request.UpdateRoleSalaryConfigRequest;
import hotel_booking.dto.response.ApiResponse;
import hotel_booking.dto.response.RoleSalaryConfigResponse;
import hotel_booking.service.RoleSalaryConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/role-salary-configs")
public class AdminRoleSalaryConfigController {

    private final RoleSalaryConfigService roleSalaryConfigService;

    // GET LIST ROLE SALARY CONFIG=====================================================
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleSalaryConfigResponse>>> getAllRoleSalaryConfigs(
            @RequestParam(required = false) Boolean isActive
    ) {
        return ResponseEntity.ok(ApiResponse.success(roleSalaryConfigService.getAllRoleSalaryConfigs(isActive)));
    }

    // CREATE ROLE SALARY CONFIG=====================================================
    @PostMapping
    public ResponseEntity<ApiResponse<RoleSalaryConfigResponse>> createRoleSalaryConfig(
            @Valid @RequestBody CreateRoleSalaryConfigRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(roleSalaryConfigService.createRoleSalaryConfig(request)));
    }

    // UPDATE ROLE SALARY CONFIG=====================================================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleSalaryConfigResponse>> updateRoleSalaryConfig(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateRoleSalaryConfigRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(roleSalaryConfigService.updateRoleSalaryConfig(id, request)));
    }
}
