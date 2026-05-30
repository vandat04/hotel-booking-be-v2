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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {

    List<RoomType> findByStatus(Integer status);

    Optional<RoomType> findByName(String name);

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

    @Query("""
            SELECT rt FROM RoomType rt
            WHERE rt.status = 1
            AND (
                (:bookingType = 'DAILY' AND rt.pricePerDay BETWEEN :minPrice AND :maxPrice)
                OR (:bookingType = 'HOURLY' AND rt.pricePerHour BETWEEN :minPrice AND :maxPrice)
            )
            AND (:adults IS NULL OR rt.maxAdults >= :adults)
            AND (:children IS NULL OR rt.maxChildren >= :children)
            AND (
                :checkIn IS NULL OR :checkOut IS NULL OR
                EXISTS (
                    SELECT r FROM Room r
                    WHERE r.roomType = rt
                      AND r.isActive = true
                      AND r.status != 'MAINTENANCE'
                      AND NOT EXISTS (
                          SELECT rs FROM RoomSchedule rs
                          WHERE rs.room = r
                            AND rs.status IN ('SCHEDULED', 'ACTIVE', 'HOLD')
                            AND NOT (rs.endAt <= :checkIn OR rs.startAt >= :checkOut)
                      )
                )
            )
            """)
    Page<RoomType> searchRoomTypes(
            @Param("bookingType") String bookingType,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("adults") Integer adults,
            @Param("children") Integer children,
            @Param("checkIn") LocalDateTime checkIn,
            @Param("checkOut") LocalDateTime checkOut,
            Pageable pageable
    );

}
