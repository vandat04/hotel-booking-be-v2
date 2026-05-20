package hotel_booking.dto.request;

import lombok.Data;

@Data
public class WalkInBookingRequest {
    private CheckAvailabilityRequest availabilityRequest;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String notes;
}
