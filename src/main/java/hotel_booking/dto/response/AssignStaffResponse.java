package hotel_booking.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
public class AssignStaffResponse {

    private Integer id;

    // USER
    private Integer userId;
    private String fullName;
    private String email;
    private String role;

    // ROLE CONFIG
    private Integer staffRoleId;
    private String staffRole;
    private Double baseSalary;

    // SHIFT
    private Integer shiftId;
    private String shiftName;
    private LocalTime startTime;
    private LocalTime endTime;

    // WORK
    private LocalDate workDate;
}