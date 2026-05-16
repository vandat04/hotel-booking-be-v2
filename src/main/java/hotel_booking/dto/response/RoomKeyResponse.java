package hotel_booking.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomKeyResponse {
    private Integer id;
    private String codeNumber;
    private String qrCodeData;
    private String status;
    private LocalDateTime activatedAt;
    private LocalDateTime expiredAt;
}
