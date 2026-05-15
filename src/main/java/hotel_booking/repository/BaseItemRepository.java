package hotel_booking.repository;

import hotel_booking.entity.BaseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseItemRepository extends JpaRepository<BaseItem, Integer> {
}
