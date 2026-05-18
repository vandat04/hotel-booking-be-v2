package hotel_booking.repository;

import hotel_booking.entity.RoomSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomScheduleRepository extends JpaRepository<RoomSchedule, Integer> {

    @Query("""
            SELECT COUNT(DISTINCT rs.room.id)
            FROM RoomSchedule rs
            WHERE rs.room.roomType.id = :roomTypeId
            AND rs.status IN ('SCHEDULED','ACTIVE', 'HOLD')
            AND NOT (
                rs.endAt <= :checkIn
                OR rs.startAt >= :checkOut
            )
            """)
    int countOverlappingRooms(
            Integer roomTypeId,
            LocalDateTime checkIn,
            LocalDateTime checkOut
    );

    List<RoomSchedule> findByBooking_Id(Integer bookingId);

    boolean existsByRoomId(Integer roomId);

    List<RoomSchedule> findByBookingId(Integer bookingId);

    @Query("""
                SELECT COUNT(DISTINCT rs.room.id)
                FROM RoomSchedule rs
                WHERE rs.status IN (
                    'SCHEDULED',
                    'ACTIVE'
                )
                AND rs.room.isActive = true
                AND rs.room.status != 'MAINTENANCE'
            """)
    long countOccupiedRooms();

}
