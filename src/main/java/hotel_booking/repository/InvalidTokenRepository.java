package hotel_booking.repository;

import hotel_booking.entity.InvalidToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidTokenRepository extends JpaRepository<InvalidToken, Integer> {
    boolean existsByToken(String token);
}
