package hotel_booking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AdminBookingDetailResponse {
    // BOOKING=====================================================
    private Integer bookingId;
    private String bookingType;
    private String bookingSource;
    private String bookingStatus;
    private String paymentStatus;
    private Integer requestedQuantity;
    private BigDecimal totalAmount;
    private String notes;
    private LocalDateTime requestedCheckin;
    private LocalDateTime requestedCheckout;
    private LocalDateTime createdAt;

    // CUSTOMER=====================================================
    private Integer customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String customerUsername;
    private String customerAvatar;
    private String customerGender;
    private LocalDate customerDateOfBirth;

    // ROOM TYPE=====================================================
    private Integer roomTypeId;
    private String roomTypeName;
    private String roomTypeDescription;
    private BigDecimal pricePerDay;
    private BigDecimal pricePerHour;
    private Integer maxAdults;
    private Integer maxChildren;
    private Integer bedCount;
    private String bedType;
    private Double roomSizeM2;

    // ROOM SCHEDULES=====================================================
    private List<AdminRoomScheduleResponse> roomSchedules;

    // =====================================================
    // PAYMENTS
    // =====================================================

    private List<PaymentResponse> payments;
}
