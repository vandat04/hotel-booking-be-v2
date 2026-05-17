package hotel_booking.controller;

import hotel_booking.dto.request.CreateRoomRequest;
import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.request.UpdateRoomRequest;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.dto.response.RoomResponse;
import hotel_booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/rooms")
@RequiredArgsConstructor
public class AdminRoomController {

    private final RoomService roomService;

    // ================= CREATE ROOM =================
    @PostMapping
    public RoomResponse createRoom(
            @RequestBody CreateRoomRequest request
    ) {
        return roomService.createRoom(request);
    }

    // ================= UPDATE ROOM =================
    @PutMapping("/{roomId}")
    public RoomResponse updateRoom(
            @PathVariable Integer roomId,
            @RequestBody UpdateRoomRequest request
    ) {
        return roomService.updateRoom(roomId, request);
    }

    // ================= DELETE ROOM =================
    @DeleteMapping("/{roomId}")
    public String deleteRoom(
            @PathVariable Integer roomId
    ) {
        roomService.deleteRoom(roomId);
        return "Delete room success";
    }

    // ================= VIEW ALL ROOMS =================
    @GetMapping
    public PageResponse<RoomResponse> getAllRooms(
            @RequestParam(required = false)
            Integer roomTypeId,
            PaginationRequest request
    ) {
        return roomService.getAllRooms(roomTypeId, request);
    }

    // ================= ROOM DETAIL =================
    @GetMapping("/{roomId}")
    public RoomResponse getRoomDetail(
            @PathVariable Integer roomId
    ) {
        return roomService.getRoomDetail(roomId);
    }
}
