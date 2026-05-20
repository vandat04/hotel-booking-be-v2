package hotel_booking.repository;

import hotel_booking.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Book;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Optional<Booking> findByIdAndCustomerId(Integer bookingId, Integer customerId);

    Page<Booking> findByCustomerIdAndStatusIn(
            Integer customerId,
            List<String> status,
            Pageable pageable
    );

    Page<Booking> findByCustomerIdAndStatus(
            Integer customerId,
            String status,
            Pageable pageable
    );

    boolean existsByRoomTypeId(Integer roomTypeId);

    @Query("""
                SELECT b.roomType.id, COUNT(b.id)
                FROM Booking b
                WHERE YEAR(b.createdAt) = :year
                AND (:month IS NULL OR MONTH(b.createdAt) = :month)
                AND b.status = 'CHECKED_OUT'
                AND b.paymentStatus = 'PAID'
                GROUP BY b.roomType.id
            """)
    List<Object[]> countPaidCheckedOutByRoomType(
            @Param("year") Integer year,
            @Param("month") Integer month
    );

    Page<Booking> findAll(Pageable pageable);

    @Query("""
                SELECT DISTINCT b
                FROM Booking b
                LEFT JOIN b.roomSchedules rs
                LEFT JOIN rs.room r
            
                WHERE (
                       CAST(b.id AS string) LIKE %:keyword%
                    OR LOWER(b.customerName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(b.customerPhone) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(b.customerEmail) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(r.roomNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            """)
    Page<Booking> searchBookings(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
                SELECT b
                FROM Booking b
                WHERE
                    (:status IS NULL OR b.status = :status)
                AND (:paymentStatus IS NULL
                     OR b.paymentStatus = :paymentStatus)
                AND (:roomTypeId IS NULL
                     OR b.roomType.id = :roomTypeId)
                AND (:fromDate IS NULL
                     OR b.createdAt >= :fromDate)
                AND (:toDate IS NULL
                     OR b.createdAt <= :toDate)
                AND (:bookingSource IS NULL
                     OR b.bookingSource = :bookingSource)
            """)
    Page<Booking> filterBookings(
            @Param("status") String status,
            @Param("paymentStatus") String paymentStatus,
            @Param("roomTypeId") Integer roomTypeId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("bookingSource") String bookingSource,
            Pageable pageable
    );

    long count();

    long countByStatusAndRequestedCheckinBetween(
            String status,
            LocalDateTime start,
            LocalDateTime end
    );

    long countByStatus(String status);

    // TOTAL REVENUE ==========================================
    @Query("""
                SELECT COALESCE(SUM(b.totalAmount),0)
                FROM Booking b
                WHERE b.paymentStatus = 'PAID'
                AND b.status NOT IN (
                    'CANCELLED',
                    'NO_SHOW'
                )
            """)
    BigDecimal getTotalRevenue();

    // WEB + WALK-IN ==========================================
    @Query("""
                SELECT COUNT(b)
                FROM Booking b
                WHERE b.bookingSource IN (
                    'WEB',
                    'WALK-IN'
                )
                AND b.status NOT IN (
                    'CANCELLED',
                    'NO_SHOW'
                )
            """)
    long countNormalBookings();

    // OTA ====================================================
    @Query("""
                SELECT COUNT(b)
                FROM Booking b
                WHERE b.bookingSource IN (
                    'AGODA',
                    'BOOKING',
                    'EXPEDIA'
                )
                AND b.status NOT IN (
                    'CANCELLED',
                    'NO_SHOW'
                )
            """)
    long countOTABookings();

    @Query("""
                SELECT b
                FROM Booking b
                WHERE b.status = 'CONFIRMED'
                  AND b.paymentStatus = 'PAID'
                  AND b.requestedCheckin BETWEEN :now AND :limitTime
            """)
    Page<Booking> findUpcomingCheckIn(
            @Param("now") LocalDateTime now,
            @Param("limitTime") LocalDateTime limitTime,
            Pageable pageable
    );

    @Query("""
                SELECT b
                FROM Booking b
                WHERE b.status = 'CHECKED_IN'
                  AND b.requestedCheckout BETWEEN :now AND :limitTime
            """)
    Page<Booking> findUpcomingCheckOut(
            @Param("now") LocalDateTime now,
            @Param("limitTime") LocalDateTime limitTime,
            Pageable pageable
    );

    @Modifying
    @Query("""
                UPDATE Booking b
                SET b.status = 'CANCELLED',
                    b.updatedAt = CURRENT_TIMESTAMP
                WHERE b.createdAt <= :timeLimit
                  AND b.paymentStatus = 'UNPAID'
                  AND b.status = 'PENDING'
            """)
    int cancelUnpaidBookings(LocalDateTime timeLimit);

}
