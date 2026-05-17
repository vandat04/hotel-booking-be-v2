package hotel_booking.repository;

import hotel_booking.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByRoomTypeId(Integer roomTypeId);

    boolean existsByBooking_Id(Integer bookingId);

    @EntityGraph(attributePaths = {"customer", "roomType"})
    Optional<Review> findById(Integer id);

    @EntityGraph(attributePaths = {"customer", "roomType"})
    Page<Review> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "roomType"})
    Page<Review> findByHotelReplyIsNull(Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "roomType"})
    Page<Review> findByHotelReplyIsNotNull(Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "roomType"})
    @Query("""
                SELECT r FROM Review r
                WHERE
                    (
                        :keyword IS NULL
                        OR LOWER(r.customerName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(r.customer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(r.roomType.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    )
            
                    AND (:rating IS NULL OR r.rating = :rating)
            
                    AND (:fromDate IS NULL OR r.createdAt >= :fromDate)
            
                    AND (:toDate IS NULL OR r.createdAt <= :toDate)
            
                ORDER BY r.createdAt DESC
            """)
    Page<Review> searchReviews(
            @Param("keyword") String keyword,
            @Param("rating") Integer rating,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    // ===== TOTAL REVIEWS =====
    Long countBy();

    // ===== AVERAGE RATING =====
    @Query("""
                SELECT COALESCE(AVG(r.rating), 0)
                FROM Review r
            """)
    Double getAverageRating();

    // ===== UNREPLIED REVIEWS =====
    @Query("""
                SELECT COUNT(r)
                FROM Review r
                WHERE r.hotelReply IS NULL
                   OR TRIM(r.hotelReply) = ''
            """)
    Long countUnrepliedReviews();

    // ===== 1 STAR REVIEWS =====
    Long countByRating(Integer rating);
}
