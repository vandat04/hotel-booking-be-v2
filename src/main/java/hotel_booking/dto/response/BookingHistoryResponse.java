package hotel_booking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingHistoryResponse {
    private Integer bookingId;
    private String roomTypeName;
    private Integer quantity;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String bookingType;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}
