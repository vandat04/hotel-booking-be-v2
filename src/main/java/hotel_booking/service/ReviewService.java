package hotel_booking.service;

import hotel_booking.dto.request.CreateReviewRequest;
import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.request.ReplyReviewRequest;
import hotel_booking.dto.request.SearchReviewRequest;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.dto.response.ReviewResponse;
import hotel_booking.dto.response.ReviewStatisticsResponse;
import hotel_booking.entity.Booking;
import hotel_booking.entity.Review;
import hotel_booking.repository.BookingRepository;
import hotel_booking.repository.ReviewRepository;
import hotel_booking.util.PaginationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import hotel_booking.exception.AppException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public void createReview(Integer customerId, CreateReviewRequest request) {

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
        if (days > 3) {
            throw new AppException("Review period has expired (must review within 3 days of check-out)");
        }

        // ===== ONLY 1 REVIEW =====
        boolean reviewed = reviewRepository.existsByBooking_Id(booking.getId());

        if (reviewed) {
            throw new AppException("This booking has already been reviewed");
        }

        // ===== VALIDATE RATING =====
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new AppException("Rating must be between 1 and 5");
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
    private void validateComment(String comment) {
        if (comment == null || comment.isBlank()) {
            return;
        }
        String lowerComment = comment.toLowerCase();
        for (String bannedWord : BANNED_WORDS) {
            if (lowerComment.contains(
                    bannedWord.toLowerCase())) {
                throw new AppException("Comment contains inappropriate content");
            }
        }
    }

    // ==================================
    // ======= GET ALL REVIEW BOOKING  ========
    // ==================================
    public PageResponse<ReviewResponse> getAllReviews(PaginationRequest request, String replyStatus) {

        Pageable pageable = PaginationUtil.build(request);

        Page<Review> pageResult;

        // ===== DEFAULT: UNREPLIED + NEWEST =====
        if (replyStatus == null || replyStatus.isBlank() || replyStatus.equalsIgnoreCase("UNREPLIED")) {
            pageResult = reviewRepository.findByHotelReplyIsNull(pageable);
        }

        // ===== REPLIED =====
        else if (replyStatus.equalsIgnoreCase("REPLIED")) {
            pageResult = reviewRepository.findByHotelReplyIsNotNull(pageable);
        }

        // ===== ALL =====
        else {
            pageResult = reviewRepository.findAll(pageable);
        }

        List<ReviewResponse> content = pageResult.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return PageResponse.<ReviewResponse>builder()
                .content(content)
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .last(pageResult.isLast())
                .build();
    }

    public ReviewResponse toResponse(Review review) {

        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .hotelReply(review.getHotelReply())
                .repliedAt(review.getRepliedAt())
                .createdAt(review.getCreatedAt())

                // ===== CUSTOMER =====
                .customer(ReviewResponse.CustomerInfo.builder()
                        .id(review.getCustomer().getId())
                        .fullName(review.getCustomer().getFullName()) // hoặc getName()
                        .email(review.getCustomer().getEmail())
                        .build())

                // ===== ROOM TYPE =====
                .roomType(ReviewResponse.RoomTypeInfo.builder()
                        .id(review.getRoomType().getId())
                        .name(review.getRoomType().getName())
                        .roomSizeM2(review.getRoomType().getRoomSizeM2())
                        .maxAdults(review.getRoomType().getMaxAdults())
                        .maxChildren(review.getRoomType().getMaxChildren())
                        .pricePerDay(review.getRoomType().getPricePerDay())
                        .pricePerHour(review.getRoomType().getPricePerHour())
                        .build())

                .build();
    }


    // ==================================
    // ======= GET REVIEW DETAIL  ========
    // ==================================
    public ReviewResponse getReviewDetail(Integer id) {

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("REVIEW_NOT_FOUND"));

        return toResponse(review);
    }

    // ==================================
    // ======= REPLY REVIEW  ========
    // ==================================
    public void replyReview(Integer reviewId, ReplyReviewRequest request) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("REVIEW_NOT_FOUND"));

        // ===== VALIDATION =====
        if (request.getReplyContent() == null || request.getReplyContent().isBlank()) {
            throw new RuntimeException("REPLY_CONTENT_REQUIRED");
        }

        // ===== UPDATE OR CREATE REPLY =====
        review.setHotelReply(request.getReplyContent());

        // luôn update lại thời gian reply (dù là lần 1 hay update lại)
        review.setRepliedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        reviewRepository.save(review);

    }

    // ==================================
    // ======= DELETE REVIEW  ========
    // ==================================
    public void deleteReview(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("REVIEW_NOT_FOUND"));

        reviewRepository.delete(review);
    }

    // ==================================
    // ======= SEARCH REVIEW  ========
    // ==================================
    public PageResponse<ReviewResponse> searchReviews(
            SearchReviewRequest request,
            PaginationRequest pagination
    ) {

        Pageable pageable = PaginationUtil.build(pagination);

        LocalDateTime fromDate = null;
        LocalDateTime toDate = null;

        // ===== CONVERT DATE =====
        if (request.getFromDate() != null) {
            fromDate = request.getFromDate().atStartOfDay();
        }

        if (request.getToDate() != null) {
            toDate = request.getToDate().atTime(23, 59, 59);
        }

        Page<Review> pageResult = reviewRepository.searchReviews(
                request.getKeyword(),
                request.getRating(),
                fromDate,
                toDate,
                pageable
        );

        List<ReviewResponse> content = pageResult.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return PageResponse.<ReviewResponse>builder()
                .content(content)
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .last(pageResult.isLast())
                .build();
    }

    // ==================================
    // ======= REVIEW RATE STATISTIC  ========
    // ==================================
    public ReviewStatisticsResponse getReviewStatistics() {

        Long totalReviews = reviewRepository.countBy();

        Double averageRating = reviewRepository.getAverageRating();

        Long unrepliedReviews = reviewRepository.countUnrepliedReviews();

        Long oneStarReviews = reviewRepository.countByRating(1);

        return ReviewStatisticsResponse.builder()
                .totalReviews(totalReviews)
                .averageRating(
                        averageRating != null
                                ? Math.round(averageRating * 10.0) / 10.0
                                : 0.0
                )
                .unrepliedReviews(unrepliedReviews)
                .oneStarReviews(oneStarReviews)
                .build();
    }
}
