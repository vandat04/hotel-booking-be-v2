package hotel_booking.repository;

import hotel_booking.entity.SalarySheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SalarySheetRepository extends JpaRepository<SalarySheet, Integer> , JpaSpecificationExecutor<SalarySheet> {

    Optional<SalarySheet> findByUser_IdAndMonthAndYear(
            Integer userId,
            Integer month,
            Integer year
    );


}
