package hotel_booking.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueStatisticsResponse {
    // ===== REVENUE =====
    private BigDecimal dailyRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal yearlyRevenue;
    // ===== ROOM =====
    private BigDecimal roomRevenue;
    // ===== OTA =====
    private BigDecimal otaRevenue;
    // ===== WALK-IN =====
    private BigDecimal walkInRevenue;
}