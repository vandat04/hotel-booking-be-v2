package hotel_booking.repository;

import hotel_booking.dto.response.RoomTypeDetailResponse;
import hotel_booking.entity.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {

    List<RoomType> findByStatus(Integer status);

    Page<RoomType> findByStatusAndPricePerDayBetween(
            Integer status,
            BigDecimal min,
            BigDecimal max,
            Pageable pageable
    );

    Page<RoomType> findByStatusAndPricePerHourBetween(
            Integer status,
            BigDecimal min,
            BigDecimal max,
            Pageable pageable
    );

    Page<RoomType> findByStatus(Integer status, Pageable pageable);

    @Query("SELECT r.pricePerDay FROM RoomType r WHERE r.id = :id")
    BigDecimal findPricePerDayById(@Param("id") Integer id);

    @Query("SELECT r.pricePerHour FROM RoomType r WHERE r.id = :id")
    BigDecimal findPricePerHourById(@Param("id") Integer id);

    boolean existsByHotelIdAndNameIgnoreCase(Integer hotelId, String name);

    Page<RoomType> findAllByStatus(
            Integer status,
            Pageable pageable
    );

}
