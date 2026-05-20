package hotel_booking.dto.response;

import lombok.*;

@Data
@Builder
public class RoomTodayStatusResponse {
    private Integer roomId;
    private String roomNumber;
    private Integer floor;
    private String roomTypeName;
    private String roomStatus;
    // READY / DIRTY / MAINTENANCE / HOLD / SCHEDULED / ACTIVE
    private String customerName; // chỉ có nếu ACTIVE
}