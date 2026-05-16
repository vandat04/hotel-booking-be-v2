package hotel_booking.repository;

import hotel_booking.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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


}
