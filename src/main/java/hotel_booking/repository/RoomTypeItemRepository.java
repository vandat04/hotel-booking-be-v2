package hotel_booking.repository;

import hotel_booking.entity.RoomTypeItem;
import hotel_booking.entity.RoomTypeItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RoomTypeItemRepository extends JpaRepository<RoomTypeItem, RoomTypeItemId> {
    List<RoomTypeItem> findByRoomTypeId(Integer roomTypeId);
}
