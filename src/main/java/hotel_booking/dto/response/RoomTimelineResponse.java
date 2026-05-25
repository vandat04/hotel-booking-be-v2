package hotel_booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTimelineResponse {
    private int roomId;
    private String roomNumber;
    private int floor;
    private String roomTypeName;
    private String status; // READY, DIRTY, MAINTENANCE
    private List<ScheduleBlock> schedules;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleBlock {
        private int scheduleId;
        private int bookingId;
        private String customerName;
        private String customerPhone;
        private String bookingType; // DAILY, HOURLY
        private LocalDateTime startAt;
        private LocalDateTime endAt;
        private String status; // HOLD, SCHEDULED, ACTIVE, COMPLETED, CANCELLED
        private boolean isLongStay;
        private boolean isOverdue;
    }
}
