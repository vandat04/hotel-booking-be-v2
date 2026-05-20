package hotel_booking.service;


import hotel_booking.dto.response.AssignStaffResponse;
import hotel_booking.entity.AssignStaff;
import hotel_booking.entity.User;
import hotel_booking.repository.AssignStaffRepository;
import hotel_booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffShiftService {

    private final AssignStaffRepository assignStaffRepository;
    private final UserRepository userRepository;

    public List<AssignStaffResponse> getMyCurrentWeekShifts(
            Integer userId
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        // ===== VALIDATE ROLE =====
        if (!List.of("RECEPTIONIST", "CLEANER").contains(user.getRole())) {
            throw new RuntimeException("ACCESS_DENIED");
        }

        // ===== CURRENT WEEK =====
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        // ===== GET ASSIGNMENTS =====
        List<AssignStaff> assignments = assignStaffRepository
                        .findByUserIdAndWorkDateBetweenOrderByWorkDateAsc(
                                user.getId(),
                                startOfWeek,
                                endOfWeek
                        );

        // ===== MAP RESPONSE =====
        return assignments.stream()
                .map(assign -> AssignStaffResponse.builder()
                        .id(assign.getId())
                        // USER
                        .userId(user.getId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .role(user.getRole())
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
}