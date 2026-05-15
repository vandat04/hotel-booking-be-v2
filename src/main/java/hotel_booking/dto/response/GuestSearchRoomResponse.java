package hotel_booking.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestSearchRoomResponse {
    private Integer roomTypeId;
    private String roomTypeName;
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
