package hotel_booking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckAvailabilityRequest {
    @NotNull(message = "Room type ID cannot be null")
    private Integer roomTypeId;

    @NotNull(message = "Check-in time cannot be null")
    private LocalDateTime checkIn;

    @NotNull(message = "Check-out time cannot be null")
    private LocalDateTime checkOut;

    @NotNull(message = "Number of rooms cannot be null")
    @Min(value = 1, message = "Number of rooms must be at least 1")
    private Integer numberOfRoom;

    @NotBlank(message = "Booking type cannot be blank")
    private String bookingType; // DAILY | HOURLY
}
