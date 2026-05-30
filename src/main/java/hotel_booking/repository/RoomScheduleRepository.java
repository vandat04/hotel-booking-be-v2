package hotel_booking.repository;

import hotel_booking.entity.RoomSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query("""
                SELECT rs
                FROM RoomSchedule rs
                JOIN FETCH rs.booking b
                JOIN FETCH b.roomType rt
                WHERE rs.status = 'SCHEDULED'
                AND FUNCTION('DATEADD', HOUR, 1, CURRENT_TIMESTAMP) = rs.startAt
            """)
    List<RoomSchedule> findUpcomingCheckIns();

    @Query("""
                SELECT rs
                FROM RoomSchedule rs
                JOIN FETCH rs.booking b
                JOIN FETCH b.roomType rt
                WHERE rs.status = 'SCHEDULED'
                AND rs.startAt BETWEEN :start AND :end
            """)
    List<RoomSchedule> findUpcomingCheckIns(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
                SELECT rs
                FROM RoomSchedule rs
                WHERE rs.status IN ('ACTIVE', 'COMPLETED')
                  AND rs.startAt <= :endOfWeek
                  AND rs.endAt >= :startOfWeek
            """)
    List<RoomSchedule> findOverlappingSchedules(
            @Param("startOfWeek") LocalDateTime startOfWeek,
            @Param("endOfWeek") LocalDateTime endOfWeek
    );

    @Query("""
                SELECT rs
                FROM RoomSchedule rs
                JOIN FETCH rs.booking b
                WHERE rs.status IN ('HOLD', 'SCHEDULED', 'ACTIVE')
                  AND rs.startAt <= CURRENT_TIMESTAMP
                  AND rs.endAt >= CURRENT_TIMESTAMP
            """)
    List<RoomSchedule> findTodayActiveSchedules();

    @Query("""
                SELECT rs
                FROM RoomSchedule rs
                JOIN FETCH rs.room r
                JOIN FETCH r.roomType rt
                JOIN FETCH rs.booking b
                WHERE rs.status IN ('HOLD', 'SCHEDULED', 'ACTIVE')
                  AND rs.startAt < :endOfDay
                  AND rs.endAt > :startOfDay
            """)
    List<RoomSchedule> findSchedulesOverlappingDay(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    @Query("""
                SELECT rs
                FROM RoomSchedule rs
                JOIN FETCH rs.room r
                JOIN FETCH rs.booking b
                WHERE (b.status = 'CHECKED_DAMAGE_ROOM' OR r.status = 'DIRTY')
                AND rs.status IN ('ACTIVE', 'COMPLETED')
                AND rs.id = (
                    SELECT MAX(rs2.id)
                    FROM RoomSchedule rs2
                    WHERE rs2.room.id = r.id
                )
                ORDER BY rs.updatedAt DESC
            """)
    Page<RoomSchedule> findRoomsNeedCleaning(Pageable pageable);

    @Modifying
    @Query("""
                UPDATE RoomSchedule rs
                SET rs.status = 'CANCELLED',
                    rs.updatedAt = CURRENT_TIMESTAMP
                WHERE rs.booking.id IN (
                    SELECT b.id
                    FROM Booking b
                    WHERE b.createdAt <= :timeLimit
                      AND b.paymentStatus = 'UNPAID'
                      AND b.status = 'PENDING'
                )
            """)
    int cancelRoomSchedules(LocalDateTime timeLimit);

    @Query("""
                SELECT rs
                FROM RoomSchedule rs
                JOIN FETCH rs.room r
                JOIN FETCH r.roomType rt
                WHERE rs.status IN ('ACTIVE', 'SCHEDULED')
                  AND rs.startAt <= :end
                  AND rs.endAt >= :start
            """)
    List<RoomSchedule> findActiveScheduledSchedulesBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT rs FROM RoomSchedule rs WHERE rs.room.id = :roomId AND rs.status = 'ACTIVE'")
    List<RoomSchedule> findActiveSchedulesByRoomId(@Param("roomId") Integer roomId);
}
