package hotel_booking.dto.request;

import lombok.Data;

@Data
public class CreateRoomRequest {
    private Integer roomTypeId;
    private String roomNumber;
    private Integer floor;
    private String allocatedFor;
}
