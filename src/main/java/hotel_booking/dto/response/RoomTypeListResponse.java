package hotel_booking.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomTypeListResponse {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal pricePerDay;
    private BigDecimal pricePerHour;
    private Integer maxAdults;
    private Integer maxChildren;
    private Integer bedCount;
    private String bedType;
    private Double roomSizeM2;
    private String thumbnail;
}
