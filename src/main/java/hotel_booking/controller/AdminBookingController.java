package hotel_booking.controller;

import hotel_booking.dto.request.CancelBookingRequest;
import hotel_booking.dto.request.FilterBookingRequest;
import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.request.SearchBookingRequest;
import hotel_booking.dto.response.AdminBookingDetailResponse;
import hotel_booking.dto.response.AdminBookingResponse;
import hotel_booking.dto.response.ApiResponse;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {

    private final BookingService bookingService;

    // ================= VIEW ALL BOOKINGS =================
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AdminBookingResponse>>> getAllBookings(
            PaginationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getAllBookings(request)));
    }

    // ================= SEARCH BOOKINGS =================
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<AdminBookingResponse>>> searchBookings(
            @ModelAttribute SearchBookingRequest request,
            @ModelAttribute PaginationRequest pagination
    ) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.searchBookings(request, pagination)));
    }

    // ================= FILTER BOOKINGS =================
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<PageResponse<AdminBookingResponse>>> filterBookings(
            @ModelAttribute FilterBookingRequest request,
            @ModelAttribute PaginationRequest pagination
    ) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.filterBookings(request, pagination)));
    }

    // ================= BOOKING DETAIL =================
    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<AdminBookingDetailResponse>> getAdminBookingDetail(
            @PathVariable Integer bookingId
    ) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getAdminBookingDetail(bookingId)));
    }

    // ================= CANCEL BOOKING =================
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelBooking(
            @PathVariable Integer bookingId,
            @RequestBody CancelBookingRequest request
    ) {
        bookingService.cancelBooking(bookingId, request);
        return ResponseEntity.ok(ApiResponse.success("Cancel booking success"));
    }
}
