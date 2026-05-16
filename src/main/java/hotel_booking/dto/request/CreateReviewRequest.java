package hotel_booking.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReviewRequest {
    private Integer bookingId;
    private Integer rating;
    private String comment;
}
