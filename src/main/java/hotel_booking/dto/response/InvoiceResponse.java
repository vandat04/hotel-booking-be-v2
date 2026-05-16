package hotel_booking.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponse {
    private Integer id;
    private String invoiceNumber;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private BigDecimal amountPaid;
    private String invoiceDescription;
    private LocalDateTime issuedAt;
    private Boolean isSentEmail;
}
