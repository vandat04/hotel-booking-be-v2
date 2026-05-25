package hotel_booking.controller;

import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.request.DamageReportRequest;
import hotel_booking.dto.response.ApiResponse;
import hotel_booking.dto.response.CleanerRoomResponse;
import hotel_booking.dto.response.DamageItemResponse;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.service.CleanerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cleaner/rooms")
@RequiredArgsConstructor
public class CleanerRoomController {

    private final CleanerService cleanerService;

    // 1. Cleaner Task
    @GetMapping("/need-cleaning")
    public ResponseEntity<ApiResponse<PageResponse<CleanerRoomResponse>>> getRoomsNeedCleaning(
            @ModelAttribute PaginationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(cleanerService.getRoomsNeedCleaning(request)));
    }

    // 2. Lấy Danh Sách Vật Dụng Để Báo Cáo Hư Hỏng
    @GetMapping("/bookings/{bookingId}/damage-items")
    public ResponseEntity<ApiResponse<List<DamageItemResponse>>> getDamageItems(
            @PathVariable Integer bookingId
    ) {
        return ResponseEntity.ok(ApiResponse.success(cleanerService.getDamageItemsForBooking(bookingId)));
    }

    // 3. Gửi Báo Cáo Hư Hỏng
    @PostMapping("/bookings/{bookingId}/damages")
    public ResponseEntity<ApiResponse<String>> submitDamages(
            @PathVariable Integer bookingId,
            @Valid @RequestBody DamageReportRequest request
        ) {
        return ResponseEntity.ok(ApiResponse.success(cleanerService.submitDamageReport(bookingId, request)));
    }

    // 4. Hoàn Thành Công Việc Dọn Dẹp
    @PostMapping("/complete")
    public ResponseEntity<ApiResponse<String>> completeCleaning(
            @RequestParam Integer bookingId
    ) {
        return ResponseEntity.ok(ApiResponse.success(cleanerService.completeCleaningByBooking(bookingId)));
    }
}

