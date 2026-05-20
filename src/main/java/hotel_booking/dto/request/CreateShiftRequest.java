package hotel_booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateShiftRequest {

    @NotBlank(message = "SHIFT_NAME_REQUIRED")
    private String shiftName;

    @NotNull(message = "START_TIME_REQUIRED")
    private LocalTime startTime;

    @NotNull(message = "END_TIME_REQUIRED")
    private LocalTime endTime;

    private String description;
}
