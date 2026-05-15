package hotel_booking.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelAmenityResponse {
    private Integer id;
    private String name;
    private String description;
}
