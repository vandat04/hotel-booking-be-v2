package hotel_booking.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CleanerRoomResponse {
    private Integer bookingId;
    private Integer roomId;
    private String roomNumber;
    private String roomTypeName;

    private String customerName;
    private String customerPhone;

    private LocalDateTime checkin;
    private LocalDateTime checkout;

    private String roomStatus;
    private String scheduleStatus;
    private String bookingStatus;
    private boolean hasDamageReport;
}
