package hotel_booking.dto.request;

import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileByAdminRequest {
    private String email;
    private String fullName;
    private String password;
    private String phone;
    private String gender;
    private LocalDate dateOfBirth;
    private String role;
    private Integer status;
}
