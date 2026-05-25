package hotel_booking.repository;

import hotel_booking.entity.RoomDamage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomDamageRepository extends JpaRepository<RoomDamage, Integer> {

    boolean existsByItemId(Integer itemId);

    boolean existsByBookingId(Integer bookingId);

    java.util.List<RoomDamage> findByBookingId(Integer bookingId);
}