package hotel_booking.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomResponse {
    private Integer id;
    private String roomNumber;
    private Integer floor;
    private String allocatedFor;
    private String status;
    private Boolean isActive;
    private Integer roomTypeId;
    private String roomTypeName;
}
