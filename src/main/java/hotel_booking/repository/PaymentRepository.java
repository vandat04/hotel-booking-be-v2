package hotel_booking.repository;

import hotel_booking.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByTransactionReference(String transactionReference);

    List<Payment> findByBooking_Id(Integer bookingId);

}
