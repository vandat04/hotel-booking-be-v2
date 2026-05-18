package hotel_booking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BookingStatisticsResponse {
    // BOOKING
    private Long totalBookings;
    // BOOKING WEB + WALK-IN
    private Long totalNormalBookings;
    // BOOKING OTA
    private Long totalOTABookings;
    // CHECK-IN TODAY
    private Long checkedInToday;
    // CANCELLED
    private Long cancelledBookings;
    // REVENUE
    private BigDecimal totalRevenue;
    // OCCUPANCY
    private Double occupancyRate;
    // ROOM
    private Long totalActiveRooms;
    private Long occupiedRooms;
}
