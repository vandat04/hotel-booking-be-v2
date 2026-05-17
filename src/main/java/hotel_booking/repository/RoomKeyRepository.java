package hotel_booking.repository;

import hotel_booking.entity.RoomKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RoomKeyRepository extends JpaRepository<RoomKey, Integer> {

    Optional<RoomKey> findByRoomSchedule_Id(Integer roomScheduleId);

    Optional<RoomKey> findByRoomScheduleId(Integer roomScheduleId);
}
