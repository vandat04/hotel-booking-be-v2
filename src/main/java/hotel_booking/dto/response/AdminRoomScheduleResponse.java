package hotel_booking.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminRoomScheduleResponse {
    private Integer roomScheduleId;
    private Integer roomId;
    private String roomNumber;
    private Integer floor;
    private String roomStatus;
    private String allocatedFor;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String status;

    // ===== ROOM KEY =====
    private Integer roomKeyId;
    private String codeNumber;
    private String qrCodeData;
    private String roomKeyStatus;
    private LocalDateTime activatedAt;
    private LocalDateTime expiredAt;
}
