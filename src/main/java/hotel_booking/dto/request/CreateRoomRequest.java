package hotel_booking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateRoomRequest {
    @NotNull(message = "Room type ID cannot be null")
    private Integer roomTypeId;

    @NotBlank(message = "Room number cannot be blank")
    private String roomNumber;

    @NotNull(message = "Floor cannot be null")
    @Min(value = 0, message = "Floor must be at least 0")
    private Integer floor;

    @NotBlank(message = "Allocated for cannot be blank")
    private String allocatedFor;
}
