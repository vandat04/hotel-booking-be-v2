package hotel_booking.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DamageReportRequest {
    @Valid
    private List<DamageItem> damages;

    @Data
    public static class DamageItem {
        @NotNull(message = "itemId không được để trống")
        private Integer itemId;

        @NotNull(message = "quantity không được để trống")
        @Min(value = 1, message = "Số lượng hỏng/mất phải lớn hơn hoặc bằng 1")
        private Integer quantity;

        @NotNull(message = "actualDamageFee không được để trống")
        @Min(value = 0, message = "Phí bồi thường không được âm")
        private BigDecimal actualDamageFee;

        private String note;
        private String evidenceImageUrl;
    }
}
