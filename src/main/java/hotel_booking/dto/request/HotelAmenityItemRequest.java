package hotel_booking.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelAmenityItemRequest {
    private String name;
    private String description;
}
