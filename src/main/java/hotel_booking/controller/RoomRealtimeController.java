package hotel_booking.controller;

import hotel_booking.dto.response.ApiResponse;
import hotel_booking.dto.response.RoomTypeTodayResponse;
import hotel_booking.service.RoomRealtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/receptionist/rooms")
@RequiredArgsConstructor
public class RoomRealtimeController {

    private final RoomRealtimeService service;

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<RoomTypeTodayResponse>>> getTodayRooms() {
        return ResponseEntity.ok(ApiResponse.success(service.getTodayRoomStatus()));
    }
}

