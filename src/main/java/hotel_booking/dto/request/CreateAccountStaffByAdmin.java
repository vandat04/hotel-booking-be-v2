package hotel_booking.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateAccountStaffByAdmin {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String gender;
    private String role;
    private LocalDate dateOfBirth;
}
