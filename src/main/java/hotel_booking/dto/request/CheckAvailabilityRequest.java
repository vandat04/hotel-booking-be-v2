package hotel_booking.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CheckAvailabilityRequest {
    private Integer roomTypeId;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Integer numberOfRoom;
    private String bookingType; // DAILY | HOURLY
}
