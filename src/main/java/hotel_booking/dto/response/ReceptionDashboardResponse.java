package hotel_booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceptionDashboardResponse {

    // OCCUPANCY
    private int totalActiveRooms;
    private long occupiedRooms;
    private int readyRooms;
    private double occupancyRate;

    // REVENUE
    private BigDecimal todayRevenue;
    private BigDecimal yesterdayRevenue;
    private double revenueGrowthPercent;

    // UPCOMING CHECKIN
    private List<UpcomingCheckInDTO> upcomingCheckIns;
}