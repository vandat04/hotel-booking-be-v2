package hotel_booking.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePaymentStatusRequest {
    private String status;
    private String notes;
}
