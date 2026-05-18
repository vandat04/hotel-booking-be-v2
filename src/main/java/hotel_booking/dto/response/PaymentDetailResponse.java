package hotel_booking.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDetailResponse {

    // ===== PAYMENT =====
    private Integer paymentId;
    private BigDecimal amount;
    private String paymentMethod;
    private String gatewayName;
    private String paymentType;
    private String status;
    private String transactionReference;
    private LocalDateTime paymentDate;
    private String notes;

    // ===== BOOKING =====
    private Integer bookingId;
    private String bookingType;
    private String bookingSource;
    private String bookingStatus;
    private String paymentStatus;
    private BigDecimal totalBookingAmount;
    private LocalDateTime requestedCheckin;
    private LocalDateTime requestedCheckout;

    // ===== CUSTOMER =====
    private Integer customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;

    // ===== ROOM =====
    private String roomTypeName;
    private Integer requestedQuantity;
    private List<RoomDetail> rooms;

    // ===== INVOICE =====
    private Integer invoiceId;

    // ===== INNER CLASS =====
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoomDetail {
        private Integer roomId;
        private String roomNumber;
        private LocalDateTime startAt;
        private LocalDateTime endAt;
        private String status;
    }
}
