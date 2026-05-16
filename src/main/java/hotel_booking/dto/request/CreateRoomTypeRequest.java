package hotel_booking.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRoomTypeRequest {
    // BASIC
    private String name;
    private String description;
    private Integer status;

    // PRICE
    private BigDecimal pricePerDay;
    private BigDecimal pricePerHour;

    // TARGET
    private Integer targetDailyPercentage;
    private Integer targetHourlyPercentage;

    // ROOM INFO
    private Integer maxAdults;
    private Integer maxChildren;
    private Integer bedCount;
    private String bedType;
    private Double roomSizeM2;
}
