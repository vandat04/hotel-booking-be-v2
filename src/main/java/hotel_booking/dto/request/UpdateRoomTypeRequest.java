package hotel_booking.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRoomTypeRequest {
    // ===== BASIC INFO =====
    @NotBlank
    @Size(max = 100)
    private String name;

    private String description;

    @Min(0)
    @Max(1)
    private Integer status;

    // ===== PRICE =====
    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal pricePerDay;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal pricePerHour;

    // ===== TARGET =====
    @Min(0)
    @Max(100)
    private Integer targetDailyPercentage;

    @Min(0)
    @Max(100)
    private Integer targetHourlyPercentage;

    // ===== ROOM INFO =====
    @Min(1)
    private Integer maxAdults;

    @Min(0)
    private Integer maxChildren;

    @Min(1)
    private Integer bedCount;

    private String bedType;

    @Positive
    private Double roomSizeM2;
}
