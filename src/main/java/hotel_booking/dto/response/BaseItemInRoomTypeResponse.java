package hotel_booking.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseItemInRoomTypeResponse {

    private Integer itemId;
    private String itemName;
    private String itemImageUrl;
    private BigDecimal baseUnitPrice;
    private Integer quantity; // số lượng trong phòng type
}
