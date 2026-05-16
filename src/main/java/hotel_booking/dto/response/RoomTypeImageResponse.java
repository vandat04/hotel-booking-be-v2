package hotel_booking.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomTypeImageResponse {
    private Integer id;
    private String imageUrl;
    private Boolean isPrimary;
    private String caption;
}