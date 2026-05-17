package hotel_booking.dto.request;

import lombok.Data;

@Data
public class UpdateRoomRequest {
    private Integer roomTypeId;
    private String roomNumber;
    private Integer floor;
    private String allocatedFor;
    private String status;
    private Boolean isActive;
}
