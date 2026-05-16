package hotel_booking.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRoomTypeImageRequest {
    private Boolean isPrimary;
    private String caption;
}
