package hotel_booking.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PayDamageRequest {
    @NotNull(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod; // CASH, ONLINE

    private String notes;
}
