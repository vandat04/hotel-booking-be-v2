package hotel_booking.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelResponse {
    private Integer id;
    private String name;
    private String description;
    private Double starRating;
    private String address;
    private String phoneNumber;
    private String email;
    private String mapUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<HotelImageResponse> images;
    private List<HotelAmenityResponse> amenities;
}