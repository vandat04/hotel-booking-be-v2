package hotel_booking.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class CheckAvailabilityResponse {
    private Boolean available;
    private Integer totalRooms;
    private Integer occupiedRooms;
    private Integer availableRooms;
    private BigDecimal totalAmount;
    private List<Integer> listRoomCanBook;
    private String message;
}