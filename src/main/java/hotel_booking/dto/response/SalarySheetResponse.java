package hotel_booking.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalarySheetResponse {

    private Integer salarySheetId;

    private Integer userId;

    private String fullName;

    private String role;

    private Integer month;

    private Integer year;

    private Long attendanceCount;

    private BigDecimal salaryPerShift;

    private BigDecimal totalSalary;

    private String status;

    private LocalDateTime createAt;

    private LocalDateTime updatedAt;
}