package hotel_booking.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelImageMetadata {
    private String caption;
    private Boolean isPrimary;
}
