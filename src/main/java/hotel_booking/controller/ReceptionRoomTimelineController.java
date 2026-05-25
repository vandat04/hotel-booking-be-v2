package hotel_booking.controller;

import hotel_booking.dto.response.ApiResponse;
import hotel_booking.dto.response.RoomTimelineResponse;
import hotel_booking.service.RoomTimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/receptionist/rooms")
@RequiredArgsConstructor
public class ReceptionRoomTimelineController {

    private final RoomTimelineService roomTimelineService;

    @GetMapping("/timeline")
    public ResponseEntity<ApiResponse<List<RoomTimelineResponse>>> getRoomTimeline(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(value = "floor", required = false) Integer floor,
            @RequestParam(value = "roomTypeId", required = false) Integer roomTypeId
    ) {
        if (date == null) {
            date = LocalDate.now();
        }
        List<RoomTimelineResponse> timeline = roomTimelineService.getRoomTimeline(date, floor, roomTypeId);
        return ResponseEntity.ok(ApiResponse.success("Successfully fetched room timeline", timeline));
    }
}
