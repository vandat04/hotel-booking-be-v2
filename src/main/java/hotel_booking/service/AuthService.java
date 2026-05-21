package hotel_booking.service;

import hotel_booking.dto.request.RegisterRequest;
import hotel_booking.dto.request.ResetPasswordRequest;
import hotel_booking.dto.response.LoginResponse;
import hotel_booking.dto.response.UserProfileResponse;
import hotel_booking.entity.InvalidToken;
import hotel_booking.entity.ResetPasswordOTP;
import hotel_booking.entity.User;
import hotel_booking.exception.AppException;
import hotel_booking.exception.DuplicateDataException;
import hotel_booking.repository.InvalidTokenRepository;
import hotel_booking.repository.ResetPasswordOTPRepository;
import hotel_booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResetPasswordOTPRepository otpRepository;
    private final EmailService emailService;
    private final InvalidTokenRepository invalidTokenRepository;

    // =============================
    public void validateAge(LocalDate dateOfBirth) {

        if (dateOfBirth == null) {
            throw new RuntimeException("Date of birth is required");
        }

        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();

        if (age < 18) {
            throw new RuntimeException("User must be at least 18 years old");
        }
    }

    public String register(RegisterRequest request) {

        // ================= CHECK AGE =================
        validateAge(request.getDateOfBirth());

        // ================= CHECK EMAIL =================
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // ================= CHECK USERNAME =================
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // ================= CHECK PHONE =================
        if (request.getPhone() != null
                && userRepository.existsByPhone(request.getPhone())) {

            throw new RuntimeException("Phone number already exists");
        }

        // ================= CREATE USER =================
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())

                // Profile
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                // Auth
                .provider("LOCAL")
                .emailVerified(false)
                // Role
                .role("CUSTOMER")
                // Status
                .status(1)
                .build();

        // ================= SAVE =================
        userRepository.save(user);

        return "Register success!";
    }

    // =============================
    @Autowired
    private JwtService jwtService;

    public String login(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Wrong password");
        }

        return jwtService.generateToken(user.getId(), user.getRole());
    }

// =============================

    private String generateOTP() {
        Random rand = new Random();
        int otp = 100000 + rand.nextInt(900000);
        return String.valueOf(otp);
    }

    public void forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));
        String otp = generateOTP();
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(5);

        Optional<ResetPasswordOTP> existingOtp = otpRepository.findByUserId(user.getId());

        if (existingOtp.isPresent()) {
            ResetPasswordOTP otpEntity = existingOtp.get();
            otpEntity.setOtpCode(otp);
            otpEntity.setExpiredAt(expiredAt);
            otpEntity.setIsUsed(false);
            otpRepository.save(otpEntity);
        } else {
            ResetPasswordOTP otpEntity = new ResetPasswordOTP();
            otpEntity.setUserId(user.getId());
            otpEntity.setOtpCode(otp);
            otpEntity.setExpiredAt(expiredAt);
            otpEntity.setIsUsed(false);
            otpRepository.save(otpEntity);
        }

        // TODO: gửi email
        emailService.sendOTP(user.getEmail(), otp);
    }

    public void resetPassword(ResetPasswordRequest request) {

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Password không khớp");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        ResetPasswordOTP otp = otpRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy OTP"));

        if (otp.getIsUsed()) {
            throw new RuntimeException("OTP đã được sử dụng");
        }

        if (otp.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP đã hết hạn");
        }

        if (!otp.getOtpCode().equals(request.getOtp())) {
            throw new RuntimeException("OTP không đúng");
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Mark OTP used
        otp.setIsUsed(true);
        otpRepository.save(otp);
    }

    public void logout(String token) {

        LocalDateTime expiry = jwtService.getExpiration(token).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        InvalidToken invalidToken = new InvalidToken();
        invalidToken.setToken(token);
        invalidToken.setExpiryTime(expiry);

        invalidTokenRepository.save(invalidToken);
    }

    // ================= LOGIN → RETURN LoginResponse =================
    public LoginResponse loginAndBuildResponse(String username, String password) {
        String accessToken = login(username, password);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException("User not found"));
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getRole());
        return buildLoginResponse(accessToken, refreshToken, user);
    }

    // ================= GOOGLE LOGIN → RETURN LoginResponse =================
    public LoginResponse loginWithGoogle(String email, String name, String picture, String sub) {
        User user = userRepository.findByUsername(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(email);
                    newUser.setEmail(email);
                    newUser.setFullName(name);
                    newUser.setAvatarUrl(picture);
                    newUser.setProvider("GOOGLE");
                    newUser.setProviderId(sub);
                    newUser.setEmailVerified(true);
                    newUser.setRole("CUSTOMER");
                    return userRepository.save(newUser);
                });

        String accessToken = jwtService.generateToken(user.getId(), user.getRole());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getRole());
        return buildLoginResponse(accessToken, refreshToken, user);
    }

    // ================= BUILD LoginResponse HELPER =================
    private LoginResponse buildLoginResponse(String accessToken, String refreshToken, User user) {
        UserProfileResponse profile = UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .emailVerified(user.getEmailVerified())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole())
                .status(user.getStatus())
                .build();

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(profile)
                .build();
    }
}
