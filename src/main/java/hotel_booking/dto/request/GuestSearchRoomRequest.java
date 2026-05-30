package hotel_booking.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuestSearchRoomRequest extends PaginationRequest {
    // DAILY | HOURLY
    private String bookingType;
    // khoảng giá
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    // optional
    private Integer adults;
    private Integer children;
    // check-in, check-out dates
    private String checkIn;
    private String checkOut;
}
