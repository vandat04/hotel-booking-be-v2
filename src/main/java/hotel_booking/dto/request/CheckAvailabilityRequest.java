package hotel_booking.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckAvailabilityRequest {
    private Integer roomTypeId;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Integer numberOfRoom;
    private String bookingType; // DAILY | HOURLY
}
