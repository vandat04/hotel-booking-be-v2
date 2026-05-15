package hotel_booking.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomOption {

    private Integer roomId;
    private String roomNumber;

    // optional: giúp FE hiển thị ưu tiên
    private Integer priorityScore;
}