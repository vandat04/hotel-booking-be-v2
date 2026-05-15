package hotel_booking.dto.request;

import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {
    private String email;
    private String fullName;
    private String phone;
    private String gender;
    private LocalDate dateOfBirth;
}
