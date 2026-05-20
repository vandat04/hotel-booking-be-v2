package hotel_booking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WalkInBookingResponse {
    private Integer bookingId;
    private String status;
    private String message;
    private Integer numberOfRooms;
    private BigDecimal totalAmount;
}
