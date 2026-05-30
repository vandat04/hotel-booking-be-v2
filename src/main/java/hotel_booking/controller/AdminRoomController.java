package hotel_booking.controller;

import hotel_booking.dto.request.CreateRoomRequest;
import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.request.UpdateRoomRequest;
import hotel_booking.dto.response.ApiResponse;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.dto.response.RoomResponse;
import hotel_booking.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/rooms")
@RequiredArgsConstructor
public class AdminRoomController {

    private final RoomService roomService;

    // ================= CREATE ROOM =================
    // POST /api/admin/rooms → 201 CREATED
    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(
            @Valid @RequestBody CreateRoomRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Room created successfully", roomService.createRoom(request)));
    }

    // ================= UPDATE ROOM =================
    // PUT /api/admin/rooms/{roomId}
    @PutMapping("/{roomId}")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(
            @PathVariable Integer roomId,
            @Valid @RequestBody UpdateRoomRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Room updated successfully", roomService.updateRoom(roomId, request)));
    }

    // ================= DELETE ROOM =================
    // DELETE /api/admin/rooms/{roomId}
    @DeleteMapping("/{roomId}")
    public ResponseEntity<ApiResponse<String>> deleteRoom(
            @PathVariable Integer roomId
    ) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.ok(ApiResponse.success("Room deleted successfully"));
    }

    // ================= VIEW ALL ROOMS (paginated, filterable) =================
    // GET /api/admin/rooms?roomTypeId=1&filterActive=all&page=0&size=10
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RoomResponse>>> getAllRooms(
            @RequestParam(required = false) Integer roomTypeId,
            @RequestParam(required = false) String filterActive,
            PaginationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getAllRooms(roomTypeId, filterActive, request)));
    }

    // ================= ROOM DETAIL =================
    // GET /api/admin/rooms/{roomId}
    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomDetail(
            @PathVariable Integer roomId
    ) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getRoomDetail(roomId)));
    }
}
