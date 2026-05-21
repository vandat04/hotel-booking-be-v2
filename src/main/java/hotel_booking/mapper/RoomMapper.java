package hotel_booking.mapper;

import hotel_booking.dto.request.CreateRoomRequest;
import hotel_booking.dto.response.RoomResponse;
import hotel_booking.entity.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public Room toEntity(CreateRoomRequest request) {
        if (request == null) {
            return null;
        }
        return Room.builder()
                .roomNumber(request.getRoomNumber())
                .floor(request.getFloor())
                .allocatedFor(request.getAllocatedFor())
                .status("READY")
                .isActive(true)
                .build();
    }

    public RoomResponse toResponse(Room room) {
        if (room == null) {
            return null;
        }
        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .floor(room.getFloor())
                .allocatedFor(room.getAllocatedFor())
                .status(room.getStatus())
                .isActive(room.getIsActive())
                .roomTypeId(room.getRoomType() != null ? room.getRoomType().getId() : null)
                .roomTypeName(room.getRoomType() != null ? room.getRoomType().getName() : null)
                .build();
    }
}
