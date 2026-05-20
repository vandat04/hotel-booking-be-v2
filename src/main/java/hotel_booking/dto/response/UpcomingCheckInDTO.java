package hotel_booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpcomingCheckInDTO {
    private Long bookingId;
    private String customerName;
    private String phone;
    private LocalDateTime startAt;
    private String roomTypeName;
}
