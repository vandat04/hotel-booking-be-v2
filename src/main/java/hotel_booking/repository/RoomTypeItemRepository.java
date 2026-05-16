package hotel_booking.repository;

import hotel_booking.entity.RoomTypeItem;
import hotel_booking.entity.RoomTypeItemId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomTypeItemRepository extends JpaRepository<RoomTypeItem, RoomTypeItemId> {
    List<RoomTypeItem> findByRoomTypeId(Integer roomTypeId);

    @Query("""
        SELECT rti
        FROM RoomTypeItem rti
        JOIN rti.item i
        WHERE rti.roomType.id = :roomTypeId
    """)
    Page<RoomTypeItem> findByRoomTypeId(@Param("roomTypeId") Integer roomTypeId, Pageable pageable);

    Optional<RoomTypeItem> findByRoomTypeIdAndItemId(Integer roomTypeId, Integer itemId);

}
