package hotel_booking.repository;

import hotel_booking.entity.CleaningTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CleaningTaskRepository extends JpaRepository<CleaningTask, Integer> {

    boolean existsByRoomId(Integer roomId);
}
