package hotel_booking.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SearchReviewRequest {
    private String keyword;
    private Integer rating;
    private LocalDate fromDate;
    private LocalDate toDate;
}
