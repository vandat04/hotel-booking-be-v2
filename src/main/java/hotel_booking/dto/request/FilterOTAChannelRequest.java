package hotel_booking.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterOTAChannelRequest {
    private String keyword;
    private Boolean isActive;
}
