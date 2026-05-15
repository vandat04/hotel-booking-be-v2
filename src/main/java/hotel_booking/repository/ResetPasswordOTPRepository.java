package hotel_booking.repository;

import hotel_booking.entity.ResetPasswordOTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetPasswordOTPRepository extends JpaRepository<ResetPasswordOTP, Integer> {

    Optional<ResetPasswordOTP> findByUserId(Integer userId);
}
