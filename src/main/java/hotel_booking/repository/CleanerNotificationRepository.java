package hotel_booking.repository;

import hotel_booking.entity.CleanerNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CleanerNotificationRepository extends JpaRepository<CleanerNotification, Integer> {
    List<CleanerNotification> findAllByOrderByCreatedAtDesc();
    List<CleanerNotification> findByCleanerIdOrderByCreatedAtDesc(Integer cleanerId);
}
