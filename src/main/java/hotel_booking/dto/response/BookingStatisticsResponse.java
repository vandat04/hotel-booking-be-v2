package hotel_booking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BookingStatisticsResponse {
    // ===== BOOKING =====
    private Long totalBookings;
    private Long checkedInToday;
    private Long cancelledBookings;
    // ===== ROOM =====
    private Double occupancyRate;
    // ===== REVENUE =====
    private BigDecimal totalRevenue;
}
