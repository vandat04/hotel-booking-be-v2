package hotel_booking.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
public class RoomTypeTodayResponse {
    private Integer roomTypeId;
    private String roomTypeName;
    private List<RoomTodayStatusResponse> rooms;
}
