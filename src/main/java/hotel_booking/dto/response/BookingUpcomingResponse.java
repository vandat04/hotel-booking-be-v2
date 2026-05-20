package hotel_booking.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BookingUpcomingResponse {
    private Integer id;
    private String customerName;
    private String customerPhone;

    private String roomTypeName;
    private Integer requestedQuantity;

    private LocalDateTime requestedCheckin;
    private LocalDateTime requestedCheckout;

    private String status;
    private String paymentStatus;

    private String bookingSource;
}
