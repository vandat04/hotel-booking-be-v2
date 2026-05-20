package hotel_booking.repository;

import hotel_booking.entity.RoomKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomKeyRepository extends JpaRepository<RoomKey, Integer> {

    Optional<RoomKey> findByRoomSchedule_Id(Integer roomScheduleId);

    Optional<RoomKey> findByRoomScheduleId(Integer roomScheduleId);

    List<RoomKey> findByRoomSchedule_Booking_Id(Integer bookingId);

    @Query("""
                SELECT rk FROM RoomKey rk
                WHERE rk.status = 'ACTIVE'
                AND rk.expiredAt <= :now
            """)
    List<RoomKey> findExpiredKeys(LocalDateTime now);
}
