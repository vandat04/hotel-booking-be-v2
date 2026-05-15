package hotel_booking.service;

import hotel_booking.dto.request.ChangePasswordRequest;
import hotel_booking.dto.request.UpdateProfileRequest;
import hotel_booking.dto.response.UserProfileResponse;
import hotel_booking.entity.User;
import hotel_booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;


    // ================= GET PROFILE =================
    public UserProfileResponse getMyProfile(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserProfileResponse.builder()
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
    }

    // ================= UPDATE PROFILE =================
    public UserProfileResponse updateProfile(Integer userId, UpdateProfileRequest request) {

        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found"));

        // ===== CHECK EMAIL DUPLICATE =====
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            boolean emailExists = userRepository.findByEmail(request.getEmail()).isPresent();
            if (emailExists) {
                throw new RuntimeException("Email already exists");
            } else {
                user.setEmail(request.getEmail());
            }
        }

        // ===== CHECK PHONE DUPLICATE =====
        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            boolean phoneExists = userRepository.findByPhone(request.getPhone()).isPresent();
            if (phoneExists) {
                throw new RuntimeException("Phone already exists");
            } else {
                user.setPhone(request.getPhone());
            }
        }

        // ===== CHECK AGE >= 18 =====
        if (request.getDateOfBirth() != null) {
            int age = Period.between(request.getDateOfBirth(), LocalDate.now()).getYears();
            if (age < 18) {
                throw new RuntimeException("User must be at least 18 years old");
            } else {
                user.setDateOfBirth(request.getDateOfBirth());
            }
        }

        // ===== UPDATE =====
        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            user.setFullName(request.getFullName().trim());
        }

        if (request.getGender() != null) {
            if (!request.getGender().equals("MALE") && !request.getGender().equals("FEMALE")) {
                throw new RuntimeException("Invalid gender");
            }
            user.setGender(request.getGender());
        }

        userRepository.save(user);

        // ===== RESPONSE =====
        return getMyProfile(userId);
    }

    // ================= UPDATE AVATAR PROFILE =================
    public UserProfileResponse updateAvatar(Integer userId, MultipartFile file) {

        // ===== CHECK USER =====
        User user = userRepository.findById(userId).orElseThrow(() ->
                        new RuntimeException("User not found"));

        // ===== VALIDATE FILE =====
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is required");
        }

        // ===== VALIDATE IMAGE TYPE =====
        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files allowed");
        }

        // ===== UPLOAD IMAGE =====
        String avatarUrl = cloudinaryService.uploadFile1(file);

        // ===== UPDATE DB =====
        user.setAvatarUrl(avatarUrl);

        userRepository.save(user);

        return getMyProfile(userId);
    }

    // ================= CHANGE PASSWORD =================
    public String changePassword(Integer userId, ChangePasswordRequest request
    ) {

        // ===== FIND USER =====
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // ===== CHECK OLD PASSWORD =====
        boolean isOldPasswordCorrect = passwordEncoder.matches( request.getOldPassword(), user.getPasswordHash());

        if (!isOldPasswordCorrect) { throw new RuntimeException( "Old password is incorrect");}

        // ===== CHECK NEW PASSWORD MATCH =====
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Confirm password does not match");}

        // ===== CHECK SAME PASSWORD =====
        boolean isSameOldPassword = passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash());

        if (isSameOldPassword) {
            throw new RuntimeException("New password must be different from old password");}

        // ===== ENCODE NEW PASSWORD =====
        String encodedPassword =passwordEncoder.encode(request.getNewPassword());

        // ===== UPDATE PASSWORD =====
        user.setPasswordHash(encodedPassword);

        userRepository.save(user);

        return "Change password successfully";
    }
}