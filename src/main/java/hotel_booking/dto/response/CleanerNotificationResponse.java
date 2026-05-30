package hotel_booking.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CleanerNotificationResponse {
    private Integer id;
    private Integer bookingId;
    private String title;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private String roomNumber; // Thêm số phòng để Frontend dễ hiển thị
}
