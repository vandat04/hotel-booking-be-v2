package hotel_booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DamageItemResponse {
    private int itemId;
    private String itemName;
    private int quantity; // Số lượng tối đa định mức của vật dụng trong phòng
    private BigDecimal baseUnitPrice; // Giá đền bù mặc định
}
