package hotel_booking.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffDashboardResponse {

    private long totalStaff;

    private long totalReceptionist;

    private long totalCleaner;

    private long totalActiveStaff;

    private long totalTodayShifts;

    private long totalAbsentToday;
}
