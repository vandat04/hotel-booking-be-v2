package hotel_booking.service;

import hotel_booking.dto.request.CreateReviewRequest;
import hotel_booking.entity.Booking;
import hotel_booking.entity.Review;
import hotel_booking.repository.BookingRepository;
import hotel_booking.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    // ==================================
    // ======= REVIEW BOOKING  ========
    // ==================================
    private static final List<String> BANNED_WORDS = List.of(
            "fuck", "shit", "dm", "clm", "ditme", "duma", "lồn", "ket"
    );

    @Transactional
    public void createReview( Integer customerId, CreateReviewRequest request) throws BadRequestException {

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // ===== VALIDATE OWNER =====
        if (booking.getCustomer() != null && !booking.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("You cannot review this booking");
        }

        // ===== ONLY CHECK_OUT =====
        if (!"CHECKED_OUT".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("Only checked-out bookings can be reviewed");
        }

        // ===== ONLY WITHIN 3 DAYS =====
        LocalDateTime checkOutTime = booking.getRequestedCheckout();
        long days = Duration.between(checkOutTime, LocalDateTime.now()).toDays();
        if (days > 3) { throw new BadRequestException("Review period expired");}

        // ===== ONLY 1 REVIEW =====
        boolean reviewed = reviewRepository.existsByBooking_Id(booking.getId());

        if (reviewed) {throw new BadRequestException("Booking already reviewed");
        }

        // ===== VALIDATE RATING =====
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        // ===== COMMUNITY VALIDATION =====
        validateComment(request.getComment());

        // ===== CREATE REVIEW =====
        Review review = Review.builder()
                .booking(booking)
                .customer(booking.getCustomer())
                .customerName(booking.getCustomerName())
                .roomType(booking.getRoomType())
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        reviewRepository.save(review);
    }

    // ===== CHECK BAD WORD =====
    private void validateComment(String comment) throws BadRequestException {
        if (comment == null || comment.isBlank()) {
            return;
        }
        String lowerComment = comment.toLowerCase();
        for (String bannedWord : BANNED_WORDS) {
            if (lowerComment.contains(
                    bannedWord.toLowerCase())) {
                throw new BadRequestException("Comment contains inappropriate content");
            }
        }
    }
}
