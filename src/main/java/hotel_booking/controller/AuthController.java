package hotel_booking.controller;

import hotel_booking.dto.request.*;
import hotel_booking.dto.response.ApiResponse;
import hotel_booking.dto.response.LoginResponse;
import hotel_booking.service.AuthService;
import hotel_booking.service.GoogleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final GoogleService googleService;

    // ================= REGISTER =================
    // POST /api/auth/register → 201 CREATED
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", authService.register(request)));
    }

    // ================= LOGIN =================
    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.loginAndBuildResponse(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));
    }

    // ================= GOOGLE LOGIN =================
    // POST /api/auth/google
    @PostMapping("/google")
    public ResponseEntity<ApiResponse<LoginResponse>> loginWithGoogle(@RequestBody GoogleLoginRequest request) {
        var payload = googleService.verifyToken(request.getIdToken());

        if (payload == null) {
            throw new hotel_booking.exception.AppException("Invalid Google token");
        }

        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String picture = (String) payload.get("picture");
        String sub = payload.getSubject();

        LoginResponse loginResponse = authService.loginWithGoogle(email, name, picture, sub);
        return ResponseEntity.ok(ApiResponse.success("Login with Google successful", loginResponse));
    }

    // ================= FORGOT PASSWORD =================
    // POST /api/auth/forgot-password
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("OTP has been sent to your email"));
    }

    // ================= RESET PASSWORD =================
    // POST /api/auth/reset-password
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully"));
    }

    // ================= LOGOUT =================
    // POST /api/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }
}
