package hotel_booking.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDashboardResponse {

    // ===== REVENUE =====
    private BigDecimal totalRevenue;

    private BigDecimal todayRevenue;

    private BigDecimal monthlyRevenue;

    // ===== TRANSACTION =====
    private Long totalTransactions;

    // ===== REFUND =====
    private BigDecimal totalRefund;

    // ===== OTA =====
    private BigDecimal otaRevenue;

    // ===== OCCUPANCY =====
    private BigDecimal occupancyRevenue;
}