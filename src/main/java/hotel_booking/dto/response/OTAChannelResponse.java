package hotel_booking.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OTAChannelResponse {
    private Integer id;
    private String name;
    private String apiKeySecret;
    private String webhookSecret;
    private Boolean isActive;
}