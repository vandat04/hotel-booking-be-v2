package hotel_booking.controller;

import hotel_booking.dto.request.UpdateRoleSalaryConfigRequest;
import hotel_booking.dto.response.RoleSalaryConfigResponse;
import hotel_booking.service.RoleSalaryConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/role-salary-configs")
public class AdminRoleSalaryConfigController {

    private final RoleSalaryConfigService roleSalaryConfigService;

    // GET LIST ROLE SALARY CONFIG=====================================================
    @GetMapping
    public List<RoleSalaryConfigResponse> getAllRoleSalaryConfigs(
            @RequestParam(required = false)
            Boolean isActive
    ) {
        return roleSalaryConfigService.getAllRoleSalaryConfigs(isActive);
    }

    // UPDATE ROLE SALARY CONFIG=====================================================
    @PutMapping("/{id}")
    public RoleSalaryConfigResponse updateRoleSalaryConfig(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateRoleSalaryConfigRequest request
    ) {
        return roleSalaryConfigService.updateRoleSalaryConfig(id, request);
    }
}
