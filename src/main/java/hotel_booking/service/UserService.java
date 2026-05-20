package hotel_booking.service;

import hotel_booking.dto.request.*;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.dto.response.StaffDashboardResponse;
import hotel_booking.dto.response.UserProfileResponse;
import hotel_booking.entity.User;
import hotel_booking.repository.AssignStaffRepository;
import hotel_booking.repository.AttendanceRepository;
import hotel_booking.repository.UserRepository;
import hotel_booking.util.AdminPaginationUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final AssignStaffRepository assignStaffRepository;
    private final AttendanceRepository attendanceRepository;


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

    // ==================== CREATE ACCOUNT FOR STAFF
    public String createStaffAccount(CreateAccountStaffByAdmin request) {
        // ================= CHECK AGE =================
        authService.validateAge(request.getDateOfBirth());

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
                .role(request.getRole())
                // Status
                .status(1)
                .build();

        // ================= SAVE =================
        userRepository.save(user);

        return "Register success!";
    }


    // ================= UPDATE PROFILE BY ADMIN =================
    public UserProfileResponse updateProfileByAdmin(Integer userId, UpdateProfileByAdminRequest request) {

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

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRole() != null) {
            if (!request.getRole().equals("CLEANER") && !request.getRole().equals("RECEPTIONIST")) {
                throw new RuntimeException("Invalid role");
            }
            user.setRole(request.getRole());
        }

        if (request.getStatus() != null) {
            if (request.getStatus() != 1 && request.getStatus() != 2 ) {
                throw new RuntimeException("Invalid status");
            }
            user.setStatus(request.getStatus());
        }
        userRepository.save(user);

        // ===== RESPONSE =====
        return getMyProfile(userId);
    }

    // ================= VIEW STAFF LIST BY ADMIN =================
    public PageResponse<UserProfileResponse> getStaffs(
            AdminStaffListRequest request
    ) {

        Pageable pageable = AdminPaginationUtil.build(request);

        String keyword = request.getKeyword();

        if (keyword != null) {
            keyword = keyword.trim();
        }

        Page<User> page = userRepository.getStaffs(
                keyword,
                request.getRole(),
                request.getStatus(),
                pageable
        );

        List<UserProfileResponse> content = page.getContent()
                .stream()
                .map(user -> {

                    UserProfileResponse response =
                            new UserProfileResponse();

                    response.setId(user.getId());
                    response.setUsername(user.getUsername());
                    response.setEmail(user.getEmail());
                    response.setEmailVerified(user.getEmailVerified());
                    response.setFullName(user.getFullName());
                    response.setPhone(user.getPhone());
                    response.setAvatarUrl(user.getAvatarUrl());
                    response.setGender(user.getGender());
                    response.setDateOfBirth(user.getDateOfBirth());
                    response.setRole(user.getRole());
                    response.setStatus(user.getStatus());

                    return response;
                })
                .toList();

        return PageResponse.<UserProfileResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }


    // ================= VIEW STAFF DETAIL =================
    public UserProfileResponse getStaffDetail(Integer userId) {

        // ===== FIND USER =====
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        // ===== CHECK STAFF ROLE =====
        if (!user.getRole().equals("CLEANER") && !user.getRole().equals("RECEPTIONIST")) {
            throw new RuntimeException("User is not staff");
        }

        // ===== RESPONSE =====
        UserProfileResponse response = new UserProfileResponse();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setEmailVerified(user.getEmailVerified());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setGender(user.getGender());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setRole(user.getRole());
        response.setStatus(user.getStatus());

        return response;
    }

    //=== DASHBOARD STAFF==========================
    public StaffDashboardResponse getStaffDashboard() {

        // ================= TODAY =================
        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay =
                today.atStartOfDay();

        LocalDateTime endOfDay =
                today.atTime(LocalTime.MAX);

        // ================= STAFF =================
        long totalStaff =
                userRepository.countByRoleInAndStatus(
                        List.of("RECEPTIONIST", "CLEANER"),
                        1
                );

        long totalReceptionist =
                userRepository.countByRoleAndStatus(
                        "RECEPTIONIST",
                        1
                );

        long totalCleaner =
                userRepository.countByRoleAndStatus(
                        "CLEANER",
                        1
                );

        long totalActiveStaff = totalStaff;

        // ================= TODAY SHIFTS =================
        long totalTodayShifts =
                assignStaffRepository.countByWorkDate(today);

        // ================= ABSENT =================
        long totalAbsentToday =
                attendanceRepository.countByStatusAndCreatedAtBetween(
                        "ABSENT",
                        startOfDay,
                        endOfDay
                );

        // ================= RESPONSE =================
        return StaffDashboardResponse.builder()
                .totalStaff(totalStaff)
                .totalReceptionist(totalReceptionist)
                .totalCleaner(totalCleaner)
                .totalActiveStaff(totalActiveStaff)
                .totalTodayShifts(totalTodayShifts)
                .totalAbsentToday(totalAbsentToday)
                .build();
    }
}