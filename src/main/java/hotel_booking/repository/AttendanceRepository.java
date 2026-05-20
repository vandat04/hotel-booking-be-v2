package hotel_booking.repository;

import hotel_booking.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer>, JpaSpecificationExecutor<Attendance> {

    Optional<Attendance> findByShiftAssignmentId(Integer shiftAssignmentId);

    Page<Attendance> findByUserId(Integer userId, Pageable pageable);

    long countByUser_IdAndStatusInAndCheckInBetween(
            Integer userId,
            List<String> statuses,
            LocalDateTime start,
            LocalDateTime end
    );

    long countByStatusAndCreatedAtBetween(
            String status,
            LocalDateTime start,
            LocalDateTime end
    );
}
