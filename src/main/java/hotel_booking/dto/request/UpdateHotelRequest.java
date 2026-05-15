package hotel_booking.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateHotelRequest {
    private String name;
    private String description;
    private Double starRating;
    private String address;
    private String phoneNumber;
    private String email;
    private String mapUrl;
}
