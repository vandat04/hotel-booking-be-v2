package hotel_booking.controller;

import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.response.CleanerRoomResponse;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.service.CleanerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cleaner/rooms")
@RequiredArgsConstructor
public class CleanerRoomController {

    private final CleanerService cleanerService;

    // 1. Cleaner Task
    @GetMapping("/need-cleaning")
    public ResponseEntity<PageResponse<CleanerRoomResponse>> getRoomsNeedCleaning(
            @ModelAttribute PaginationRequest request
    ) {
        return ResponseEntity.ok(cleanerService.getRoomsNeedCleaning(request));
    }

    //3. Hoàn Thành Công Việc
    @PostMapping("/complete")
    public ResponseEntity<String> completeCleaning(
            @RequestParam Integer bookingId
    ) {
        return ResponseEntity.ok(cleanerService.completeCleaningByBooking(bookingId));
    }
}
