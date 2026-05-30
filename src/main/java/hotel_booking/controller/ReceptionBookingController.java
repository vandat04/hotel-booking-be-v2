package hotel_booking.controller;

import hotel_booking.dto.request.*;
import hotel_booking.dto.response.*;
import hotel_booking.service.BookingService;
import hotel_booking.service.ReceptionBookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/receptionist/bookings")
@RequiredArgsConstructor
public class ReceptionBookingController {

    private final ReceptionBookingService service;
    private final BookingService bookingService;

    // 1. CHECK AVAILABILITY
    // POST /api/receptionist/bookings/check
    @PostMapping("/check")
    public ResponseEntity<ApiResponse<CheckAvailabilityResponse>> check(
            @Valid @RequestBody CheckAvailabilityRequest req
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.checkAvailability(req)));
    }

    // 2. WALK-IN BOOKING
    // POST /api/receptionist/bookings/walk-in → 201 CREATED
    @PostMapping("/walk-in")
    public ResponseEntity<ApiResponse<WalkInBookingResponse>> createWalkIn(
            @Valid @RequestBody WalkInBookingRequest req
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Walk-in booking created successfully", service.createWalkInBooking(req)));
    }

    // 3. GET ALL BOOKINGS (Newest first, paginated)
    // GET /api/receptionist/bookings?page=0&size=10
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AdminBookingResponse>>> getAllBookings(
            PaginationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.getAllBookings(request)));
    }

    // 4. GET BOOKING DETAIL
    // GET /api/receptionist/bookings/{bookingId}
    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<AdminBookingDetailResponse>> getBookingDetail(
            @PathVariable Integer bookingId
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.getBookingDetail(bookingId)));
    }

    // 5. SEARCH BOOKINGS
    // GET /api/receptionist/bookings/search?keyword=&page=0&size=10
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<AdminBookingResponse>>> searchBookings(
            @ModelAttribute SearchBookingRequest request,
            @ModelAttribute PaginationRequest pagination
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.searchBookings(request, pagination)));
    }

    // 6. CANCEL BOOKING
    // PUT /api/receptionist/bookings/{bookingId}/cancel
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelBooking(
            @PathVariable Integer bookingId,
            @Valid @RequestBody CancelBookingRequest request
    ) {
        service.cancelBooking(bookingId, request);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully"));
    }

    // 7. REFUND BOOKING
    // PUT /api/receptionist/bookings/{bookingId}/refund
    @PutMapping("/{bookingId}/refund")
    public ResponseEntity<ApiResponse<String>> refundBooking(
            @PathVariable Integer bookingId
    ) {
        service.refundBooking(bookingId);
        return ResponseEntity.ok(ApiResponse.success("Refund processed successfully"));
    }

    // 8. PAYMENT (cash/bank transfer at reception)
    // POST /api/receptionist/bookings/pay
    @PostMapping("/pay")
    public ResponseEntity<ApiResponse<String>> createReceptionPayment(
            @Valid @RequestBody ReceptionistPaymentRequest request
    ) {
        String result = service.createPaymentBookingByReceptionist(request);
        return ResponseEntity.ok(ApiResponse.success("Payment recorded successfully", result));
    }

    // 9. CHECK-IN BOOKING
    // POST /api/receptionist/bookings/{bookingId}/check-in
    @PostMapping("/{bookingId}/check-in")
    public ResponseEntity<ApiResponse<String>> checkInBooking(
            @PathVariable Integer bookingId
    ) {
        return ResponseEntity.ok(ApiResponse.success("Check-in successful", service.checkInBooking(bookingId)));
    }

    // 10. CHECK-OUT BOOKING
    // POST /api/receptionist/bookings/{bookingId}/check-out
    @PostMapping("/{bookingId}/check-out")
    public ResponseEntity<ApiResponse<String>> checkOutBooking(
            @PathVariable Integer bookingId
    ) {
        return ResponseEntity.ok(ApiResponse.success("Check-out successful", service.checkOutBooking(bookingId)));
    }

    // 11. UPCOMING CHECK-INS (paginated)
    // GET /api/receptionist/bookings/upcoming-checkin
    @GetMapping("/upcoming-checkin")
    public ResponseEntity<ApiResponse<PageResponse<BookingUpcomingResponse>>> getUpcomingCheckIns(
            @ModelAttribute PaginationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getUpcomingCheckIns(request)));
    }

    // 12. UPCOMING CHECK-OUTS (paginated)
    // GET /api/receptionist/bookings/upcoming-checkout
    @GetMapping("/upcoming-checkout")
    public ResponseEntity<ApiResponse<PageResponse<BookingUpcomingResponse>>> getUpcomingCheckOut(
            @ModelAttribute PaginationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(bookingService.getUpcomingCheckOuts(request)));
    }

    // 13. GET BOOKING DAMAGES
    // GET /api/receptionist/bookings/{bookingId}/damages
    @GetMapping("/{bookingId}/damages")
    public ResponseEntity<ApiResponse<BookingDamageResponse>> getBookingDamages(
            @PathVariable Integer bookingId
    ) {
        return ResponseEntity.ok(ApiResponse.success(service.getBookingDamages(bookingId)));
    }

    // 14. PAY BOOKING DAMAGES
    // POST /api/receptionist/bookings/{bookingId}/pay-damages
    @PostMapping("/{bookingId}/pay-damages")
    public ResponseEntity<ApiResponse<String>> payBookingDamages(
            @PathVariable Integer bookingId,
            @Valid @RequestBody PayDamageRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", service.payBookingDamages(bookingId, request)));
    }

    // 15. REQUEST ROOM CLEAN FOR CLEANER
    // POST /api/receptionist/bookings/rooms/{roomId}/request-clean
    @PostMapping("/rooms/{roomId}/request-clean")
    public ResponseEntity<ApiResponse<String>> requestRoomClean(
            @PathVariable Integer roomId
    ) {
        return ResponseEntity.ok(ApiResponse.success("Request sent successfully", service.requestRoomClean(roomId)));
    }
}
