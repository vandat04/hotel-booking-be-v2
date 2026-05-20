package hotel_booking.dto.request;

import lombok.Data;

@Data
public class SalaryFilterRequest {

    // Filter theo tháng
    private Integer month;

    private Integer year;

    // Filter theo nhân viên
    private Integer userId;

    // Filter theo role
    private String role;

    // Filter theo trạng thái
    private String status;
}
