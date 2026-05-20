package hotel_booking.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleSalaryConfigRequest {

    @NotNull(message = "BASE_SALARY_REQUIRED")
    @DecimalMin(value = "0", message = "BASE_SALARY_INVALID")
    private BigDecimal baseSalary;

    @NotNull(message = "IS_ACTIVE_REQUIRED")
    private Boolean isActive;
}
