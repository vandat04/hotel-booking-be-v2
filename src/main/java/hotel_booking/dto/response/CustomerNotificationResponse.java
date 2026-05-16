package hotel_booking.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerNotificationResponse {
    private Integer id;
    private Integer bookingId;
    private String title;
    private String message;
    private String notificationType;
    private Boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
