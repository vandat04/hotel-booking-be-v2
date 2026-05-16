package hotel_booking.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Integer id;
    private BigDecimal amount;
    private String paymentMethod;
    private String gatewayName;
    private String paymentType;
    private String status;
    private String transactionReference;
    private LocalDateTime paymentDate;
    private String notes;
}
