package hotel_booking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AdminBookingResponse {
    private Integer id;
    // ===== CUSTOMER =====
    private Integer customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    // ===== ROOM TYPE =====
    private Integer roomTypeId;
    private String roomTypeName;
    // ===== BOOKING =====
    private Integer requestedQuantity;
    private LocalDateTime requestedCheckin;
    private LocalDateTime requestedCheckout;
    private String bookingType;
    private String bookingSource;
    private String status;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private LocalDateTime createdAt;
}
