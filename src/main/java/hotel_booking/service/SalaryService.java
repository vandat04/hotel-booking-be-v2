package hotel_booking.service;

import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.request.SalaryFilterRequest;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.dto.response.SalarySheetResponse;
import hotel_booking.entity.*;
import hotel_booking.repository.*;
import hotel_booking.util.PaginationUtil;
import hotel_booking.util.SalarySpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryService {

    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final BonusPenaltyRepository bonusPenaltyRepository;
    private final RoleSalaryConfigRepository roleSalaryConfigRepository;
    private final SalarySheetRepository salarySheetRepository;

    @Transactional
    public List<SalarySheetResponse> calculateMonthlySalaryForAllStaff(
            Integer month,
            Integer year
    ) {
        // ================= VALIDATE =================
        if (month < 1 || month > 12) {
            throw new RuntimeException("INVALID_MONTH");
        }
        // ================= GET STAFF =================
        List<User> staffs = userRepository.findByRoleInAndStatus(List.of("RECEPTIONIST", "CLEANER"),1);
        // ================= TIME RANGE =================
        LocalDateTime startDate = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime endDate = LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth()).atTime(LocalTime.MAX);
        List<SalarySheetResponse> responses = new ArrayList<>();
        // ================= LOOP STAFF =================
        for (User user : staffs) {
            // ===== ROLE SALARY =====
            RoleSalaryConfig salaryConfig = roleSalaryConfigRepository
                    .findByStaffRoleAndIsActiveTrue(user.getRole()).orElse(null);
            if (salaryConfig == null) {
                continue;
            }

            // ===== ATTENDANCE COUNT =====
            long attendanceCount = attendanceRepository
                    .countByUser_IdAndStatusInAndCheckInBetween(
                            user.getId(),
                            List.of("PRESENT", "LATE"),
                            startDate,
                            endDate
                    );

            // ===== SALARY PER SHIFT =====
            BigDecimal salaryPerShift =
                    salaryConfig.getBaseSalary();

            // ===== BASE SALARY =====
            BigDecimal baseSalary =
                    salaryPerShift.multiply(
                            BigDecimal.valueOf(attendanceCount)
                    );

            // ===== FIND EXISTING =====
            SalarySheet salarySheet = salarySheetRepository
                    .findByUser_IdAndMonthAndYear(user.getId(), month, year).orElse(null);

            // ===== CREATE NEW =====
            if (salarySheet == null) {
                salarySheet = SalarySheet.builder()
                        .user(user)
                        .roleSalaryConfig(salaryConfig)
                        .month(month)
                        .year(year)
                        .status("PENDING")
                        .createdAt(LocalDateTime.now())
                        .build();
            }
            salarySheet.setTotalSalary(BigDecimal.valueOf(attendanceCount).multiply(baseSalary));
            salarySheet.setCreatedAt(LocalDateTime.now());
            // ===== UPDATE =====
            salarySheet = salarySheetRepository.save(salarySheet);

            // ===== RESPONSE =====
            responses.add(SalarySheetResponse.builder()
                    .salarySheetId(salarySheet.getId())
                    .userId(user.getId())
                    .fullName(user.getFullName())
                    .role(user.getRole())
                    .month(month)
                    .year(year)
                    .attendanceCount(attendanceCount)
                    .salaryPerShift(salaryPerShift)
                    .totalSalary(baseSalary)
                    .status(salarySheet.getStatus())
                    .build()
            );
        }

        return responses;
    }

    //=== VIEW LIST SALARY
    public PageResponse<SalarySheetResponse> getSalarySheets(
            SalaryFilterRequest filter,
            PaginationRequest pagination
    ) {

        // ===== DEFAULT SORT =====
        if (pagination.getSortBy() == null || pagination.getSortBy().isBlank()) {
            pagination.setSortBy("createdAt");
        }

        if (pagination.getDirection() == null || pagination.getDirection().isBlank()) {
            pagination.setDirection("desc");
        }

        Pageable pageable = PaginationUtil.build(pagination);

        // ===== QUERY =====
        Page<SalarySheet> salaryPage = salarySheetRepository.findAll(SalarySpecification.filterSalary(filter), pageable);

        // ===== MAP RESPONSE =====
        Page<SalarySheetResponse> responsePage = salaryPage.map(this::mapToSalarySheetResponse);

        // ===== RETURN =====
        return PageResponse.<SalarySheetResponse>builder()
                .content(responsePage.getContent())
                .page(responsePage.getNumber())
                .size(responsePage.getSize())
                .totalElements(responsePage.getTotalElements())
                .totalPages(responsePage.getTotalPages())
                .last(responsePage.isLast())
                .build();
    }

    // MAP RESPONSE =========================================================
    private SalarySheetResponse mapToSalarySheetResponse(
            SalarySheet salarySheet
    ) {

        Long attendanceCount = null;

        BigDecimal salaryPerShift = null;

        BigDecimal totalSalary = salarySheet.getTotalSalary();

        // ===== ATTENDANCE COUNT =====
        if (salarySheet.getTotalSalary() != null) {

            LocalDateTime startDate = YearMonth
                    .of(salarySheet.getYear(), salarySheet.getMonth())
                    .atDay(1)
                    .atStartOfDay();

            LocalDateTime endDate = YearMonth
                    .of(salarySheet.getYear(), salarySheet.getMonth())
                    .atEndOfMonth()
                    .atTime(23, 59, 59);

            attendanceCount =
                    attendanceRepository
                            .countByUser_IdAndStatusInAndCheckInBetween(
                                    salarySheet.getUser().getId(),
                                    List.of("PRESENT", "LATE"),
                                    startDate,
                                    endDate
                            );

            // ===== SALARY PER SHIFT =====
            if (attendanceCount > 0) {
                salaryPerShift =
                        salarySheet.getTotalSalary()
                                .divide(
                                        BigDecimal.valueOf(attendanceCount),
                                        2,
                                        RoundingMode.HALF_UP
                                );
            }


        }

        return SalarySheetResponse.builder()
                .salarySheetId(salarySheet.getId())
                .userId(salarySheet.getUser().getId())
                .fullName(salarySheet.getUser().getFullName())
                .role(salarySheet.getUser().getRole())
                .month(salarySheet.getMonth())
                .year(salarySheet.getYear())
                .attendanceCount(attendanceCount)
                .salaryPerShift(salaryPerShift)
                .totalSalary(totalSalary)
                .status(salarySheet.getStatus())
                .createAt(salarySheet.getCreatedAt())
                .updatedAt(salarySheet.getUpdatedAt())
                .build();
    }

    //=== PAY SALARY ====================================
    @Transactional
    public SalarySheetResponse updateSalaryStatusPaid(
            Integer salarySheetId
    ) {

        // ===== FIND SALARY SHEET =====
        SalarySheet salarySheet =
                salarySheetRepository
                        .findById(salarySheetId)
                        .orElseThrow(() ->
                                new RuntimeException("SALARY_SHEET_NOT_FOUND"));

        // ===== VALIDATE =====
        if ("PAID".equalsIgnoreCase(salarySheet.getStatus())) {
            throw new RuntimeException("SALARY_ALREADY_PAID");
        }

        // ===== UPDATE STATUS =====
        salarySheet.setStatus("PAID");

        salarySheet.setUpdatedAt(LocalDateTime.now());

        salarySheetRepository.save(salarySheet);

        // ===== RESPONSE =====
        return mapToSalarySheetResponse(salarySheet);
    }


    public SalarySheetResponse viewCurrentMonthSalary(Integer userId) {

        // ===== CURRENT USER =====
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // ===== VALIDATE ROLE =====
        if (!List.of("CLEANER", "RECEPTIONIST")
                .contains(user.getRole())) {
            throw new RuntimeException("ACCESS_DENIED");
        }

        // ===== CURRENT TIME =====
        LocalDate now = LocalDate.now();
        Integer month = now.getMonthValue();
        Integer year = now.getYear();

        // ===== FIND SALARY =====
        SalarySheet salarySheet =
                salarySheetRepository
                        .findByUser_IdAndMonthAndYear(
                                user.getId(),
                                month,
                                year
                        )
                        .orElseThrow(() ->
                                new RuntimeException("SALARY_NOT_FOUND"));

        // ===== RESPONSE =====
        return mapToSalarySheetResponse(salarySheet);
    }
}
