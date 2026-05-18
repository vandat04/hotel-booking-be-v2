package hotel_booking.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgodaBookingWebhookRequest {
    private String bookingId;
    private String otaChannel;
    private String otaHotelId;
    private Guest guest;
    private Room room;
    private Stay stay;
    private Pricing pricing;
    private Payment payment;
    private List<String> specialRequests;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;

    @Getter
    @Setter
    public static class Guest {
        private String fullName;
        private String phone;
        private String email;
        private String nationality;
    }

    @Getter
    @Setter
    public static class Room {
        private String roomTypeCode;
        private String roomTypeName;
        private Integer quantity;
        private Integer adults;
        private Integer children;
    }

    @Getter
    @Setter
    public static class Stay {
        private LocalDateTime checkIn;
        private LocalDateTime checkOut;
        private Integer nights;
    }

    @Getter
    @Setter
    public static class Pricing {
        private String currency;
        private BigDecimal roomAmount;
        private BigDecimal taxAmount;
        private BigDecimal serviceFee;
        private BigDecimal totalAmount;
        private BigDecimal commissionAmount;
    }

    @Getter
    @Setter
    public static class Payment {
        private String paymentType;
        private String paymentStatus;
    }
}