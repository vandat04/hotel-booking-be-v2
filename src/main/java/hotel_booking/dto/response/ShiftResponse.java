package hotel_booking.dto.response;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftResponse {

    private Integer id;

    private String shiftName;

    private LocalTime startTime;

    private LocalTime endTime;

    private String description;

    private Boolean isActive;
}
