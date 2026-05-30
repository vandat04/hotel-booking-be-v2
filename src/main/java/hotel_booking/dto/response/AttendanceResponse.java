package hotel_booking.dto.response;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AttendanceResponse {

    private Integer attendanceId;

    private Integer userId;
    private String fullName;
    private String role;

    private Integer assignId;
    private String shiftName;

    private LocalDate workDate;

    private LocalDateTime checkIn;

    private Integer lateMinutes;

    private String status;

    private String message;
}
