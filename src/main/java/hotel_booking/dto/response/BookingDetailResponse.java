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
public class BookingDetailResponse {

    // ===== BOOKING =====
    private Integer bookingId;
    private String bookingType;
    private String bookingSource;
    private String bookingStatus;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String notes;
    private LocalDateTime createdAt;

    // ===== CUSTOMER =====
    private Integer customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;

    // ===== ROOM TYPE =====
    private Integer roomTypeId;
    private String roomTypeName;
    private BigDecimal pricePerDay;
    private BigDecimal pricePerHour;

    // ===== REQUEST =====
    private Integer requestedQuantity;
    private LocalDateTime requestedCheckin;
    private LocalDateTime requestedCheckout;

    // ===== ROOM SCHEDULE =====
    private List<RoomScheduleDetailResponse> roomSchedules;

    // === PAYEMT - INVOICE ===
    private List<PaymentResponse> payments;
    private List<InvoiceResponse> invoices;
}
