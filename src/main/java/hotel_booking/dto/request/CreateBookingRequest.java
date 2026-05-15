package hotel_booking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {
    private CheckAvailabilityRequest availabilityRequest;
    private Integer customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String bookingSource;
    private String notes;
}