package hotel_booking.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private Integer id;
    private String username;
    private String email;
    private Boolean emailVerified;
    private String fullName;
    private String phone;
    private String avatarUrl;
    private String gender;
    private LocalDate dateOfBirth;
    private String role;
    private Integer status;
}
