package hotel_booking.repository;

import hotel_booking.entity.CustomerNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerNotificationRepository extends JpaRepository<CustomerNotification, Integer> {
    Page<CustomerNotification> findByUser_Id(Integer userId, Pageable pageable);
}