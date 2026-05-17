package hotel_booking.repository;

import hotel_booking.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Book;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
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
}
