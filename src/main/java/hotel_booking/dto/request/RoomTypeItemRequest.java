package hotel_booking.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeItemRequest {
    private Integer itemId;
    private Integer quantity;
}