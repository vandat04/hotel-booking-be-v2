package hotel_booking.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomScheduleDetailResponse {
    private Integer roomScheduleId;
    private Integer roomId;
    private String roomNumber;
    private Integer floor;
    private String roomStatus;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String scheduleStatus;

    // ===== ROOM KEY =====
    private Integer roomKeyId;
    private String codeNumber;
    private String qrCodeData;
    private LocalDateTime activatedAt;
    private LocalDateTime expiredAt;
    private String roomKeyStatus;
}
