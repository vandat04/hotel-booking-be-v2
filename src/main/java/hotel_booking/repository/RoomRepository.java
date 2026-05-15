package hotel_booking.repository;


import hotel_booking.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    @Query("""
            SELECT COUNT(r)
            FROM Room r
            WHERE r.roomType.id = :roomTypeId
            AND r.isActive = true
            """)
    int countTotalRooms(@Param("roomTypeId") Integer roomTypeId);

    //    @Query("""
//            SELECT r
//            FROM Room r
//            WHERE r.roomType.id = :roomTypeId
//            AND r.isActive = true
//            AND r.status = 'ACTIVE'
//            AND NOT EXISTS (
//                SELECT 1
//                FROM RoomSchedule rs
//                WHERE rs.room.id = r.id
//                AND rs.startAt < :checkOut
//                AND rs.endAt > :checkIn
//            )
//            """)
//    List<Room> findAvailableRooms(
//            Integer roomTypeId,
//            LocalDateTime checkIn,
//            LocalDateTime checkOut
//    );
    @Query("""
            SELECT r
            FROM Room r
            LEFT JOIN r.roomSchedules rs
                ON rs.startAt < :checkOut
                AND rs.endAt > :checkIn
                AND rs.status IN ('SCHEDULED','ACTIVE')
            WHERE r.roomType.id = :roomTypeId
            AND r.isActive = true
            AND r.status = 'READY'
            AND rs.id IS NULL
            """)
    List<Room> findAvailableRooms(
            Integer roomTypeId,
            LocalDateTime checkIn,
            LocalDateTime checkOut
    );
}
