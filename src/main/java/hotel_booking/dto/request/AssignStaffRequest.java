package hotel_booking.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AssignStaffRequest {

    @NotNull(message = "USER_ID_REQUIRED")
    private Integer userId;

    @NotNull(message = "STAFF_ROLE_ID_REQUIRED")
    private Integer staffId;

    @NotNull(message = "SHIFT_ID_REQUIRED")
    private Integer shiftId;

    @NotNull(message = "WORK_DATE_REQUIRED")
    @FutureOrPresent(message = "WORK_DATE_INVALID")
    private LocalDate workDate;
}
