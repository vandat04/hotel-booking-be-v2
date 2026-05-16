package hotel_booking.repository;

import hotel_booking.entity.RoomTypeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface RoomTypeImageRepository extends JpaRepository<RoomTypeImage, Integer> {

    Optional<RoomTypeImage> findFirstByRoomTypeIdAndIsPrimaryTrue(Integer roomTypeId);

    Optional<RoomTypeImage> findFirstByRoomTypeIdOrderByIdAsc(Integer roomTypeId);

    List<RoomTypeImage> findByRoomTypeId(Integer roomTypeId);

    void deleteByRoomTypeId(Integer roomTypeId);

    List<RoomTypeImage> findByRoomTypeIdOrderByIsPrimaryDescIdAsc(
            Integer roomTypeId
    );

}