package hotel_booking.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminStaffListRequest extends PaginationRequest{
    /*
       search by:
       - fullName
       - email
       - role
    */
    private String keyword;

    // ===== FILTER =====

    /*
        CLEANER
        RECEPTIONIST
     */
    private String role;

    /*
        1 ACTIVE
        2 INACTIVE
        3 BANNED
     */
    private Integer status;
}
