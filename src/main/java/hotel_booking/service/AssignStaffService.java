package hotel_booking.service;

import hotel_booking.dto.request.AssignStaffRequest;
import hotel_booking.dto.response.AssignStaffResponse;
import hotel_booking.entity.AssignStaff;
import hotel_booking.entity.RoleSalaryConfig;
import hotel_booking.entity.Shift;
import hotel_booking.entity.User;
import hotel_booking.repository.AssignStaffRepository;
import hotel_booking.repository.RoleSalaryConfigRepository;
import hotel_booking.repository.ShiftRepository;
import hotel_booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignStaffService {

    private final AssignStaffRepository assignStaffRepository;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;
    private final RoleSalaryConfigRepository roleSalaryConfigRepository;

    // ================= PHAN CA LAM VIEC =================
    @Transactional
    public AssignStaffResponse assignStaff(AssignStaffRequest request) {
        // ===== FIND USER =====
        User user = userRepository.findByIdAndRoleIn(request.getUserId(), List.of("RECEPTIONIST", "CLEANER"))
                .orElseThrow(() -> new RuntimeException("STAFF_NOT_FOUND_OR_ROLE_INVALID"));

        // ===== FIND ROLE CONFIG =====
        RoleSalaryConfig roleConfig = roleSalaryConfigRepository.findById(request.getStaffId())
                .orElseThrow(() -> new RuntimeException("ROLE_SALARY_CONFIG_NOT_FOUND"));

        // ===== VALIDATE ROLE =====
        if (!user.getRole().equals(roleConfig.getStaffRole())) {
            throw new RuntimeException("ROLE_NOT_MATCH_WITH_SALARY_CONFIG");
        }

        // ===== FIND SHIFT =====
        Shift shift = shiftRepository.findById(request.getShiftId())
                .orElseThrow(() -> new RuntimeException("SHIFT_NOT_FOUND"));

        // ===== CHECK DUPLICATE =====
        boolean exists = assignStaffRepository.existsByUserIdAndShiftIdAndWorkDate(request.getUserId(), request.getShiftId(), request.getWorkDate());

        if (exists) {
            throw new RuntimeException("STAFF_ALREADY_ASSIGNED");
        }

        // ===== CREATE =====
        AssignStaff assignStaff = new AssignStaff();

        assignStaff.setUser(user);
        assignStaff.setRoleSalaryConfig(roleConfig);
        assignStaff.setShift(shift);
        assignStaff.setWorkDate(request.getWorkDate());

        assignStaffRepository.save(assignStaff);

        // ===== RESPONSE =====
        return AssignStaffResponse.builder()
                .id(assignStaff.getId())
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .staffRoleId(roleConfig.getId())
                .staffRole(roleConfig.getStaffRole())
                .baseSalary(roleConfig.getBaseSalary().doubleValue())
                .shiftId(shift.getId())
                .shiftName(shift.getShiftName())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .workDate(assignStaff.getWorkDate())
                .build();
    }

    // ================= VIEW LIST CA LAM VIEC TRONG TUAN HIEN TAI =================
    public List<AssignStaffResponse> getCurrentWeekAssignments() {
        // ===== CURRENT DATE =====
        LocalDate today = LocalDate.now();
        // ===== START OF WEEK (MONDAY) =====
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        // ===== END OF WEEK (SUNDAY) =====
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        // ===== GET DATA =====
        List<AssignStaff> assignments = assignStaffRepository
                .findByWorkDateBetweenOrderByWorkDateAsc(startOfWeek, endOfWeek);

        // ===== MAP RESPONSE =====
        return assignments.stream()
                .map(assign -> AssignStaffResponse.builder()
                        .id(assign.getId())
                        // USER
                        .userId(assign.getUser().getId())
                        .fullName(assign.getUser().getFullName())
                        .email(assign.getUser().getEmail())
                        .role(assign.getUser().getRole())

                        // ROLE CONFIG
                        .staffRoleId(assign.getRoleSalaryConfig().getId())
                        .staffRole(assign.getRoleSalaryConfig().getStaffRole())
                        .baseSalary(assign.getRoleSalaryConfig().getBaseSalary().doubleValue())

                        // SHIFT
                        .shiftId(assign.getShift().getId())
                        .shiftName(assign.getShift().getShiftName())
                        .startTime(assign.getShift().getStartTime())
                        .endTime(assign.getShift().getEndTime())

                        // DATE
                        .workDate(assign.getWorkDate())

                        .build())
                .toList();
    }

    // DELETE CA LÀM VIỆC
    @Transactional
    public String deleteAssignStaff(Integer assignId) {
        // ===== FIND ASSIGNMENT =====
        AssignStaff assignStaff = assignStaffRepository.findById(assignId)
                .orElseThrow(() -> new RuntimeException("ASSIGN_STAFF_NOT_FOUND"));
        // ===== VALIDATE DELETE TIME =====
        LocalDate today = LocalDate.now();
    /*
        Ví dụ:
        workDate = 2026-05-25
        Chỉ được xoá trước 2026-05-24
        Nếu hôm nay là 24 hoặc 25 -> không cho xoá
     */

        if (!today.isBefore(assignStaff.getWorkDate().minusDays(1))) {
            throw new RuntimeException("CANNOT_DELETE_BEFORE_1_DAY_OF_WORK");
        }

        // ===== DELETE =====
        assignStaffRepository.delete(assignStaff);
        return "DELETE_ASSIGN_STAFF_SUCCESS";
    }
}