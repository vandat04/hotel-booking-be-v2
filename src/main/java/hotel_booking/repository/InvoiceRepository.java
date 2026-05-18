package hotel_booking.repository;

import hotel_booking.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    boolean existsByPaymentId(Integer paymentId);

    Optional<Invoice> findByPaymentId(Integer paymentId);
}
