package hotel_booking.repository;

import hotel_booking.entity.RoleSalaryConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleSalaryConfigRepository
        extends JpaRepository<RoleSalaryConfig, Integer> {

    // lấy tất cả
    List<RoleSalaryConfig> findAllByOrderByIdDesc();
 
    // lọc theo trạng thái
    List<RoleSalaryConfig> findByIsActiveOrderByIdDesc(Boolean isActive);
 
    Optional<RoleSalaryConfig> findByStaffRoleAndIsActiveTrue(String role);

    Optional<RoleSalaryConfig> findByStaffRoleIgnoreCase(String staffRole);
}