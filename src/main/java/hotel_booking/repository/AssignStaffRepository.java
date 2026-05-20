package hotel_booking.repository;

import hotel_booking.entity.AssignStaff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AssignStaffRepository extends JpaRepository<AssignStaff, Integer> {

    boolean existsByUserIdAndShiftIdAndWorkDate(
            Integer userId,
            Integer shiftId,
            LocalDate workDate
    );

    boolean existsByUserIdAndWorkDate(
            Integer userId,
            LocalDate workDate
    );

    List<AssignStaff> findByWorkDate(LocalDate workDate);

    List<AssignStaff> findByUserId(Integer userId);

    List<AssignStaff> findByShiftId(Integer shiftId);

    List<AssignStaff> findByWorkDateBetweenOrderByWorkDateAsc(
            LocalDate startDate,
            LocalDate endDate
    );

    List<AssignStaff> findByUserIdAndWorkDateBetweenOrderByWorkDateAsc(
            Integer userId,
            LocalDate startDate,
            LocalDate endDate
    );


    Optional<AssignStaff> findByUserIdAndWorkDate(Integer userId, LocalDate workDate);

    long countByWorkDate(LocalDate workDate);
}
