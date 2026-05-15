package hotel_booking.repository;

import hotel_booking.entity.RoomSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface RoomScheduleRepository extends JpaRepository<RoomSchedule, Integer> {
    //    @Query("""
//            SELECT COUNT(rs)
//            FROM RoomSchedule rs
//            WHERE rs.room.id IN (
//                SELECT r.id FROM Room r WHERE r.roomType.id = :roomTypeId
//            )
//            AND rs.status IN ('SCHEDULED', 'ACTIVE', 'COMPLETED', 'CANCELLED' )
//            AND NOT (
//                rs.endAt <= :checkIn
//                OR rs.startAt >= :checkOut
//            )
//            """)
//    int countOverlappingRooms(
//            @Param("roomTypeId") Integer roomTypeId,
//            @Param("checkIn") LocalDateTime checkIn,
//            @Param("checkOut") LocalDateTime checkOut
//    );
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
}
