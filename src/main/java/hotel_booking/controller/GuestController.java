package hotel_booking.controller;

import hotel_booking.dto.request.CheckAvailabilityRequest;
import hotel_booking.dto.request.CreateBookingRequest;
import hotel_booking.dto.request.GuestSearchRoomRequest;
import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.response.*;
import hotel_booking.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hotel")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService searchRoomService;
    private final HotelService hotelService;
    private final RoomTypeService roomTypeService;
    private final BookingService bookingService;
    private final UserService userService;

    // ================= GET USER ID =================
    public Integer getUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Integer.parseInt(authentication.getName());
    }

    // ================= VIEW HOTEL =================
    @GetMapping()
    public HotelResponse getHotelById() {
        return hotelService.getHotelById();
    }

    // ================= VIEW ROOM TYPE LIST =================
    @GetMapping("/room-types")
    public Page<RoomTypeListResponse> getAllActiveRoomTypes(PaginationRequest request) {
        return roomTypeService.getActiveRoomTypes(request);
    }

    // ================= SEARCH ROOM TYPE =================
    @GetMapping("/search-room-types")
    public PageResponse<GuestSearchRoomResponse> search(
            @RequestBody GuestSearchRoomRequest request
    ) {
        return searchRoomService.search(request);
    }

    // ================= VIEW ROOM TYPE DETAIL =================
    @GetMapping("/room-type")
    public RoomTypeDetailResponse getDetail(
            @RequestParam Integer id
    ) {
        return searchRoomService.getDetail(id);
    }

    // ================= CHECK ROOM AVAILABLE =================
    @GetMapping("/room-type/check-availability")
    public CheckAvailabilityResponse checkAvailability(
            @RequestBody CheckAvailabilityRequest request
    ) {
        return bookingService.checkAvailability(request);
    }

    // ============  CREATE BOOKING (BOOK NOW)  =============
    @PostMapping("/room-type/book-now")
    public ResponseEntity<?> createBooking(
            @RequestBody CreateBookingRequest request,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        UserProfileResponse user = userService.getMyProfile(getUserId());

        if (user.getId() != null) {
            request.setCustomerId(user.getId());
        } else {
            throw new RuntimeException("Please Login!");
        }
        if (user.getFullName() != null && user.getPhone() != null && user.getEmail() != null) {
            request.setCustomerName(user.getFullName());
            request.setCustomerPhone(user.getPhone());
            request.setCustomerEmail(user.getEmail());
        } else {
            throw new RuntimeException("Please Update Your Profile!");
        }

        // =========== CALL SERVICE (RE-CHECK INSIDE SERVICE)  ==============
        BookingResponse response = bookingService.createBooking(request);

        return ResponseEntity.ok(response);
    }
}
