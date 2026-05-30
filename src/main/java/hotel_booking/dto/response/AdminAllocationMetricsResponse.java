package hotel_booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAllocationMetricsResponse {
    private double performanceOccupancy;
    private double performanceTrend;
    private double hourlyDemand;
    private String hourlyDemandTag;
    private double expectedRevenue;
}
