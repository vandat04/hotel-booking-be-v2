package hotel_booking.repository;

import hotel_booking.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByTransactionReference(String transactionReference);

    List<Payment> findByBooking_Id(Integer bookingId);

    List<Payment> findByBookingId(Integer bookingId);

    @Query("""
            SELECT p
            FROM Payment p
            WHERE
                (
                    :keyword IS NULL
                    OR LOWER(p.transactionReference)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.gatewayName)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.paymentType)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            AND
                (
                    :status IS NULL
                    OR LOWER(p.status) = LOWER(:status)
                )
            AND
                (
                    :paymentMethod IS NULL
                    OR LOWER(p.paymentMethod)
                        = LOWER(:paymentMethod)
                )
            """)
    Page<Payment> searchPayments(
            String keyword,
            String status,
            String paymentMethod,
            Pageable pageable
    );

    @Query("""
            SELECT p
            FROM Payment p
            JOIN p.booking b
            WHERE
                (
                    :keyword IS NULL
                    OR LOWER(p.transactionReference)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.gatewayName)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(p.paymentType)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            
            AND
                (
                    :status IS NULL
                    OR LOWER(p.status) = LOWER(:status)
                )
            
            AND
                (
                    :paymentMethod IS NULL
                    OR LOWER(p.paymentMethod)
                        = LOWER(:paymentMethod)
                )
            
            AND
                (
                    :bookingSource IS NULL
                    OR LOWER(b.bookingSource)
                        = LOWER(:bookingSource)
                )
            
            AND
                (
                    :otaChannel IS NULL
                    OR LOWER(b.bookingSource)
                        = LOWER(:otaChannel)
                )
            
            AND
                (
                    :fromDate IS NULL
                    OR p.paymentDate >= :fromDate
                )
            
            AND
                (
                    :toDate IS NULL
                    OR p.paymentDate <= :toDate
                )
            
            AND
                (
                    :minAmount IS NULL
                    OR p.amount >= :minAmount
                )
            
            AND
                (
                    :maxAmount IS NULL
                    OR p.amount <= :maxAmount
                )
            """)
    Page<Payment> filterPayments(
            String keyword,
            String status,
            String paymentMethod,
            String bookingSource,
            String otaChannel,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "booking",
            "booking.roomType",
            "booking.roomSchedules",
            "booking.roomSchedules.room",
            "invoice"
    })
    Optional<Payment> findDetailById(Integer id);

    // TOTAL REVENUE=========================================================
    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Payment p
            WHERE p.status = 'SUCCESS'
            """)
    BigDecimal getTotalRevenue();

    // TODAY REVENUE =========================================================
    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Payment p
            WHERE p.status = 'SUCCESS'
            AND p.paymentDate BETWEEN :start AND :end
            """)
    BigDecimal getRevenueBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    // TOTAL TRANSACTIONS=========================================================
    @Query("""
            SELECT COUNT(p)
            FROM Payment p
            """)
    Long getTotalTransactions();

    // TOTAL REFUND =========================================================
    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Payment p
            WHERE p.status = 'REFUNDED'
            """)
    BigDecimal getTotalRefund();

    // OTA REVENUE=========================================================
    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Payment p
            WHERE p.status = 'SUCCESS'
            AND p.booking.bookingSource IN (
                'AGODA',
                'BOOKING',
                'EXPEDIA'
            )
            """)
    BigDecimal getOtaRevenue();

    // OCCUPANCY REVENUE=========================================================
    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Payment p
            WHERE p.status = 'SUCCESS'
            AND p.booking.status = 'CHECKED_OUT'
            """)
    BigDecimal getOccupancyRevenue();

    // =========================================================
    // ROOM REVENUE
    // =========================================================
    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Payment p
            WHERE p.status = 'SUCCESS'
            AND p.paymentType IN (
                'FULL_ROOM_CHARGE',
                'EXTEND_ROOM_CHARGE'
            )
            """)
    BigDecimal getRoomRevenue();

    // =========================================================
    // WALK-IN REVENUE
    // =========================================================
    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Payment p
            WHERE p.status = 'SUCCESS'
            AND p.booking.bookingSource = 'WALK-IN'
            """)
    BigDecimal getWalkInRevenue();
}
