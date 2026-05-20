package hotel_booking.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceptionistPaymentRequest {
    private Integer bookingId;
    private String paymentMethod; //CASH , ONLINE
    private String notes;
}
