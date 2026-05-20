package hotel_booking.controller;

import hotel_booking.dto.request.*;
import hotel_booking.dto.response.*;
import hotel_booking.service.BookingService;
import hotel_booking.service.ReceptionBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/receptionist/bookings")
@RequiredArgsConstructor
public class ReceptionBookingController {

    private final ReceptionBookingService service;
    private final BookingService bookingService;

    // 1. CHECK AVAILABILITY
    @PostMapping("/check")
    public ResponseEntity<CheckAvailabilityResponse> check(@RequestBody CheckAvailabilityRequest req) {
        return ResponseEntity.ok(service.checkAvailability(req));
    }

    // 2. WALK-IN BOOKING
    @PostMapping("/walk-in")
    public ResponseEntity<WalkInBookingResponse> createWalkIn(
            @RequestBody WalkInBookingRequest req
    ) {
        return ResponseEntity.ok(service.createWalkInBooking(req));
    }

    // 3. GET ALL BOOKINGS (Newest first)
    @GetMapping
    public ResponseEntity<PageResponse<AdminBookingResponse>> getAllBookings(
            PaginationRequest request
    ) {
        return ResponseEntity.ok(service.getAllBookings(request));
    }

    // 4. GET BOOKING DETAIL
    @GetMapping("/{bookingId}/detail")
    public ResponseEntity<AdminBookingDetailResponse> getBookingDetail(
            @PathVariable Integer bookingId
    ) {
        return ResponseEntity.ok(service.getBookingDetail(bookingId));
    }

    // 5. SEARCH BOOKINGS
    @GetMapping("/search")
    public ResponseEntity<PageResponse<AdminBookingResponse>> searchBookings(
            @RequestBody SearchBookingRequest request,
            PaginationRequest pagination
    ) {
        return ResponseEntity.ok(service.searchBookings(request, pagination));
    }

    // 6. CANCEL BOOKING
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<String> cancelBooking(
            @PathVariable Integer bookingId,
            @RequestBody CancelBookingRequest request
    ) {
        service.cancelBooking(bookingId, request);
        return ResponseEntity.ok("Cancel booking success");
    }

    // 7. REFUND BOOKING
    @PutMapping("/{bookingId}/refund")
    public ResponseEntity<String> refundBooking(
            @PathVariable Integer bookingId
    ) {
        service.refundBooking(bookingId);
        return ResponseEntity.ok("Refund booking success");
    }

    // 8. PAYMENT
    @PostMapping("/pay")
    public ResponseEntity<?> createReceptionPayment(
            @RequestBody ReceptionistPaymentRequest request
    ) {
        try {
            String result = service.createPaymentBookingByReceptionist(request);
            return ResponseEntity.ok(result);

        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message", ex.getMessage()
                    )
            );
        }
    }

    // 9. CHECK_IN BOOKING
    @PostMapping("/{bookingId}/check-in")
    public String checkInBooking(
            @PathVariable Integer bookingId
    ) {
        return service.checkInBooking(bookingId);
    }

    // 10. CHECK_OUT BOOKING
    @PostMapping("/{bookingId}/check-out")
    public String checkOutBooking(
            @PathVariable Integer bookingId
    ) {
        return service.checkOutBooking(bookingId);
    }

    // 11.VIEW SẮP CHECK-IN BOOKING LIST
    @GetMapping("/upcoming-checkin")
    public PageResponse<BookingUpcomingResponse> getUpcomingCheckIns(
            @ModelAttribute PaginationRequest request
    ) {
        return bookingService.getUpcomingCheckIns(request);
    }

    // 12.VIEW SẮP CHECK-OUT BOOKING LIST
    @GetMapping("/upcoming-checkout")
    public PageResponse<BookingUpcomingResponse> getUpcomingCheckOut(
            @ModelAttribute PaginationRequest request
    ) {
        return bookingService.getUpcomingCheckOuts(request);
    }
}
