package hotel_booking.controller;

import hotel_booking.dto.response.ApiResponse;
import hotel_booking.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class TestJWTController {
    private final AuthService authService;

    // ================= ADMIN =================

    @GetMapping("/admin/test")
    public ResponseEntity<ApiResponse<String>> admin(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                "Hello ADMIN : " + authentication.getName()
                + " | Role = " + authentication.getAuthorities()
        ));
    }

    // ================= CUSTOMER =================

    @GetMapping("/customer/test")
    public ResponseEntity<ApiResponse<String>> customer(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                "Hello CUSTOMER id : "
                + authentication.getName()
                + " | Role = " + authentication.getAuthorities()
        ));
    }

    // ================= CLEANER =================

    @GetMapping("/cleaner/test")
    public ResponseEntity<ApiResponse<String>> cleaner(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                "Hello CLEANER id : "
                + authentication.getName()
                + " | Role = " + authentication.getAuthorities()
        ));
    }

    // ================= RECEPTIONIST =================

    @GetMapping("/receptionist/test")
    public ResponseEntity<ApiResponse<String>> receptionist(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                "Hello RECEPTIONIST id : "
                + authentication.getName()
                + " | Role = " + authentication.getAuthorities()
        ));
    }

    // ================= STAFF =================

    @GetMapping("/staff/test")
    public ResponseEntity<ApiResponse<String>> staff(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(
                "Hello STAFF id : "
                + authentication.getName()
                + " | Role = " + authentication.getAuthorities()
        ));
    }
}
