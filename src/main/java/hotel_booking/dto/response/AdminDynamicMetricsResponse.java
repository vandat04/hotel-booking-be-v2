package hotel_booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDynamicMetricsResponse {
    private BigDecimal doanhThuHomNay;
    private BigDecimal doanhThuHomQua;
    private double soVoiHomQuaPercent;
    
    private double congSuatPhongHienTai;
    private long totalRooms;
    private long occupiedRoomsToday;
    
    private long soPhongBanHomNay;
    private long soPhongDaNhan;
    private long soPhongDaTra;
    
    private List<DailyOccupancyDto> congSuatThang;
    private List<RecentActivityDto> hoatDongGanDay;
    
    private BigDecimal doanhThuThangNay;
    private List<RoomTypeOccupancyDto> congSuatTheoLoaiPhong;
}
