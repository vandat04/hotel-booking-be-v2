package hotel_booking.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OTADashboardResponse {
    // Tổng số OTA đã liên kết
    private Long totalChannels;
    // OTA đang hoạt động
    private Long activeChannels;
    // OTA bị lỗi / dừng hoạt động
    private Long inactiveChannels;
}