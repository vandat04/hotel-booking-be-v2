package hotel_booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOTAChannelRequest {
    @NotBlank(message = "OTA name is required")
    private String name;
    private String apiKeySecret;
    private String webhookSecret;
    private Boolean isActive = true;
}