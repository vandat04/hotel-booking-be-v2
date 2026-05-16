package hotel_booking.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseItemCreateRequest {
    private String itemName;
    private BigDecimal baseUnitPrice;
    private String description;
}
