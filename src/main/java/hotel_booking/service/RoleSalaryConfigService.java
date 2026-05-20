package hotel_booking.service;

import hotel_booking.dto.request.UpdateRoleSalaryConfigRequest;
import hotel_booking.dto.response.RoleSalaryConfigResponse;
import hotel_booking.entity.RoleSalaryConfig;
import hotel_booking.repository.RoleSalaryConfigRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleSalaryConfigService {

    private final RoleSalaryConfigRepository roleSalaryConfigRepository;

    // GET LIST ROLE SALARY CONFIG=====================================================
    public List<RoleSalaryConfigResponse> getAllRoleSalaryConfigs(
            Boolean isActive
    ) {
        List<RoleSalaryConfig> configs;
        // lấy tất cả
        if (isActive == null) {
            configs = roleSalaryConfigRepository.findAllByOrderByIdDesc();
        }
        // lọc theo trạng thái
        else {
            configs = roleSalaryConfigRepository.findByIsActiveOrderByIdDesc(isActive);
        }

        return configs.stream().map(this::mapToResponse).toList();
    }

    // MAPPER=====================================================
    private RoleSalaryConfigResponse mapToResponse(
            RoleSalaryConfig config
    ) {

        return RoleSalaryConfigResponse.builder()
                .id(config.getId())
                .staffRole(config.getStaffRole())
                .baseSalary(config.getBaseSalary())
                .isActive(config.getIsActive())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }

    // UPDATE ROLE SALARY CONFIG=====================================================
    @Transactional
    public RoleSalaryConfigResponse updateRoleSalaryConfig(
            Integer id,
            UpdateRoleSalaryConfigRequest request
    ) {
        // FIND CONFIG=================================================
        RoleSalaryConfig config = roleSalaryConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ROLE_SALARY_CONFIG_NOT_FOUND"));

        // VALIDATION================================================
        if (request.getBaseSalary().doubleValue() < 0) {
            throw new RuntimeException("BASE_SALARY_INVALID");
        }

        // UPDATE=================================================
        config.setBaseSalary(request.getBaseSalary());
        config.setIsActive(request.getIsActive());

        // save
        roleSalaryConfigRepository.save(config);

        return mapToResponse(config);
    }

}
