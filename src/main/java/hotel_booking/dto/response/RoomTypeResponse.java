package hotel_booking.dto.response;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomTypeResponse {
    private Integer id;
    private Integer hotelId;
    private String name;
    private String description;
    private Integer status;
    private BigDecimal pricePerDay;
    private BigDecimal pricePerHour;
    private Integer targetDailyPercentage;
    private Integer targetHourlyPercentage;
    private Integer maxAdults;
    private Integer maxChildren;
    private Integer bedCount;
    private String bedType;
    private Double roomSizeM2;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}