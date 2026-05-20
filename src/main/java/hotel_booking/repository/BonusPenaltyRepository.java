package hotel_booking.repository;


import hotel_booking.entity.BonusPenalty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BonusPenaltyRepository extends JpaRepository<BonusPenalty, Integer> {
//    List<BonusPenalty> findBySalaryConfig_IdAndRelatedDateBetween(
//            Integer salaryId,
//            LocalDate start,
//            LocalDate end
//    );


}
