package hotel_booking.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomTypeBookingStatsResponse {
    private Integer roomTypeId;
    private String roomTypeName;
    private Long bookingCount;
    private Double percentage;
}