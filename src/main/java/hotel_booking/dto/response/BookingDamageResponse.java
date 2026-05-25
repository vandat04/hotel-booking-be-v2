package hotel_booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDamageResponse {
    private int bookingId;
    private String customerName;
    private String customerPhone;
    private String roomNumber;
    private BigDecimal totalDamageFee;
    private String bookingStatus;
    private List<DamageItemDetail> damages;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DamageItemDetail {
        private int damageId;
        private int itemId;
        private String itemName;
        private int quantity;
        private BigDecimal actualDamageFee;
        private String note;
        private String evidenceImageUrl;
        private LocalDateTime reportedAt;
    }
}
