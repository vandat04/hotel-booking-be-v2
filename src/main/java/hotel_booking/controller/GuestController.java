package hotel_booking.controller;

import hotel_booking.dto.request.CheckAvailabilityRequest;
import hotel_booking.dto.request.GuestSearchRoomRequest;
import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.response.*;
import hotel_booking.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotel")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService searchRoomService;
    private final HotelService hotelService;
    private final RoomTypeService roomTypeService;
    private final BookingService bookingService;

    // ================= VIEW HOTEL INFO =================
    // GET /api/hotel
    @GetMapping
    public ResponseEntity<ApiResponse<HotelResponse>> getHotelById() {
        return ResponseEntity.ok(ApiResponse.success(hotelService.getHotelById()));
    }

    // ================= VIEW ROOM TYPE LIST (active only, paginated) =================
    // GET /api/hotel/room-types?page=0&size=10
    @GetMapping("/room-types")
    public ResponseEntity<ApiResponse<PageResponse<RoomTypeListResponse>>> getAllActiveRoomTypes(PaginationRequest request) {
        Page<RoomTypeListResponse> page = roomTypeService.getActiveRoomTypes(request);
        PageResponse<RoomTypeListResponse> pageResponse = PageResponse.<RoomTypeListResponse>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    // ================= SEARCH ROOM TYPES (with filters) =================
    // GET /api/hotel/room-types/search?bookingType=DAILY&checkIn=2026-06-01&checkOut=2026-06-05
    //                             &adults=2&children=1&minPrice=100&maxPrice=500&page=0&size=10
    @GetMapping("/room-types/search")
    public ResponseEntity<ApiResponse<PageResponse<GuestSearchRoomResponse>>> search(
            @ModelAttribute GuestSearchRoomRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(searchRoomService.search(request)));
    }

    // ================= VIEW ROOM TYPE DETAIL =================
    // GET /api/hotel/room-types/{id}
    @GetMapping("/room-types/{id}")
    public ResponseEntity<ApiResponse<RoomTypeDetailResponse>> getDetail(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(ApiResponse.success(searchRoomService.getDetail(id)));
    }

    // ================= CHECK ROOM AVAILABILITY =================
    // GET /api/hotel/room-types/availability?roomTypeId=1&checkIn=2026-06-01&checkOut=2026-06-05
    @GetMapping("/room-types/availability")
    public ResponseEntity<ApiResponse<CheckAvailabilityResponse>> checkAvailability(
            @ModelAttribute CheckAvailabilityRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.checkAvailability(request)));
    }
}
