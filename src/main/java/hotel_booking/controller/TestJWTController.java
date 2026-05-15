package hotel_booking.controller;

import hotel_booking.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class TestJWTController {
    private final AuthService authService;

    // ================= ADMIN =================

    @GetMapping("/admin/test")
    public String admin(Authentication authentication) {

        return "Hello ADMIN : " + authentication.getName()
                + " | Role = " + authentication.getAuthorities();
    }

    // ================= CUSTOMER =================

    @GetMapping("/customer/test")
    public String customer(Authentication authentication) {

        return "Hello CUSTOMER id : "
                + authentication.getName()
                + " | Role = " + authentication.getAuthorities();
    }

    // ================= CLEANER =================

    @GetMapping("/cleaner/test")
    public String cleaner(Authentication authentication) {

        return "Hello CLEANER id : "
                + authentication.getName()
                + " | Role = " + authentication.getAuthorities();
    }

    // ================= RECEPTIONIST =================

    @GetMapping("/receptionist/test")
    public String receptionist(Authentication authentication) {

        return "Hello RECEPTIONIST id : "
                + authentication.getName()
                + " | Role = " + authentication.getAuthorities();
    }

    // ================= STAFF =================

    @GetMapping("/staff/test")
    public String staff(Authentication authentication) {

        return "Hello STAFF id : "
                + authentication.getName()
                + " | Role = " + authentication.getAuthorities();
    }

//    private final BookingService bookingService;
//
//    @PostMapping("/ota")
//    public ResponseEntity<BookingResponse> createOtaBooking(@RequestBody OtaBookingRequest otaReq) {
//        try {
//            BookingResponse response = bookingService.createOtaBooking(otaReq);
//            return ResponseEntity.ok(response);
//        } catch (RuntimeException ex) {
//            // Trả về lỗi với status 400 và message
//            BookingResponse booking = new BookingResponse();
//            booking.setBookingId(null);
//            booking.setBookingId(null);
//            booking.setBookingStatus(0);
//            booking.setAvailableRooms(0);
//            return ResponseEntity
//                    .badRequest()
//                    .body(booking); // BookingId=null, bookingStatus=0, availableRooms=0
//        }
//    }
}
