package hotel_booking.repository;


import hotel_booking.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("""
            SELECT r
            FROM Room r
            LEFT JOIN r.roomSchedules rs
                ON rs.startAt < :checkOut
                AND rs.endAt > :checkIn
                AND rs.status IN ('SCHEDULED','ACTIVE', 'HOLD')
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

    boolean existsByRoomNumber(String roomNumber);

    boolean existsByRoomNumberAndIdNot(String roomNumber, Integer id);

    Page<Room> findAllByIsActiveTrue(Pageable pageable);

    Page<Room> findByRoomTypeIdAndIsActiveTrue(
            Integer roomTypeId,
            Pageable pageable
    );

    Page<Room> findByRoomTypeId(Integer roomTypeId, Pageable pageable);

    Page<Room> findAllByIsActiveFalse(Pageable pageable);

    Page<Room> findByRoomTypeIdAndIsActiveFalse(
            Integer roomTypeId,
            Pageable pageable
    );

    long countByIsActiveTrue();

    long countByStatus(String status);

    @Query("SELECT COUNT(r) FROM Room r WHERE r.isActive = true")
    int countActiveRooms();

    @Query("SELECT COUNT(r) FROM Room r WHERE r.isActive = true AND r.status = 'READY'")
    int countReadyRooms();

    @Query("""
                SELECT r
                FROM Room r
                JOIN FETCH r.roomType
                WHERE r.isActive = true
            """)
    List<Room> findAllActiveRooms();
}
