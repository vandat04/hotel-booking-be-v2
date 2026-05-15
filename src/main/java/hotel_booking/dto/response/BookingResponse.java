package hotel_booking.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookingResponse {
    private Integer bookingId;
    private String status;
    private String message;
}
