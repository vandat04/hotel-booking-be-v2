package hotel_booking.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AttendanceFilterRequest extends PaginationRequest {

    // filter theo ngày cụ thể
    private LocalDate workDate;

    // filter theo tháng/năm
    private Integer month;

    private Integer year;

    // filter theo nhân viên
    private Integer userId;

    // filter theo ca
    private Integer shiftId;

}