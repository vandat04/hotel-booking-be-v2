package hotel_booking.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewStatisticsResponse {
    // tổng review
    private Long totalReviews;
    // rating trung bình
    private Double averageRating;
    // review chưa reply
    private Long unrepliedReviews;
    // review 1 sao
    private Long oneStarReviews;
}