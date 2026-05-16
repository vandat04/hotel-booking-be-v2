package hotel_booking.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private Integer bookingId;
    private String notes;
}
