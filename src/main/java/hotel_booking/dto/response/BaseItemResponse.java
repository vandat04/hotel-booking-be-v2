package hotel_booking.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseItemResponse {
    private Integer id;
    private String itemName;
    private BigDecimal baseUnitPrice;
    private String itemImageUrl;
    private String description;
}