package hotel_booking.service;

import hotel_booking.dto.request.AttendanceFilterRequest;
import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.response.AttendanceResponse;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.entity.AssignStaff;
import hotel_booking.entity.Attendance;
import hotel_booking.entity.BonusPenalty;
import hotel_booking.entity.User;
import hotel_booking.repository.AssignStaffRepository;
import hotel_booking.repository.AttendanceRepository;
import hotel_booking.repository.BonusPenaltyRepository;
import hotel_booking.repository.UserRepository;
import hotel_booking.util.AttendanceSpecification;
import hotel_booking.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final UserRepository userRepository;
    private final AssignStaffRepository assignStaffRepository;
    private final AttendanceRepository attendanceRepository;
    private final BonusPenaltyRepository bonusPenaltyRepository;

    // === CHECK_IN ==============================================
    @Transactional
    public AttendanceResponse checkIn(
            Integer userId
    ) {
        // ===== GET LOGIN USER =====
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
        // ===== VALIDATE ROLE =====
        if (!user.getRole().equals("RECEPTIONIST") && !user.getRole().equals("CLEANER")) {
            throw new RuntimeException("ACCESS_DENIED");
        }
        // ===== TODAY =====
        LocalDate today = LocalDate.now();
        // ===== FIND ASSIGNMENT =====
        AssignStaff assign = assignStaffRepository.findByUserIdAndWorkDate(user.getId(), today)
                .orElseThrow(() -> new RuntimeException("NO_SHIFT_ASSIGNED_TODAY"));
        // ===== CHECK ALREADY CHECK-IN =====
        boolean exists = attendanceRepository.findByShiftAssignmentId(assign.getId()).isPresent();
        if (exists) {
            throw new RuntimeException("ALREADY_CHECKED_IN");
        }
        // ===== TIME =====
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        LocalTime shiftStart = assign.getShift().getStartTime();
        // ===== CALCULATE LATE =====
        int lateMinutes = 0;
        String status = "PRESENT";
        if (currentTime.isAfter(shiftStart)) {
            lateMinutes = (int) Duration.between(shiftStart, currentTime).toMinutes();
            status = "LATE";
        }
        // ===== CREATE ATTENDANCE =====
        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setShiftAssignment(assign);
        attendance.setCheckIn(now);
        attendance.setLateMinutes(lateMinutes);
        attendance.setStatus(status);
        attendanceRepository.save(attendance);

        // ===== CREATE PENALTY =====
        if (status.equals("LATE")) {
            BonusPenalty penalty = new BonusPenalty();
            penalty.setType("PENALTY");
            penalty.setAmount(BigDecimal.valueOf(100000));
            penalty.setReason("Late check-in: " + lateMinutes + " minutes");
            penalty.setRelatedDate(today);
            bonusPenaltyRepository.save(penalty);
        }

        // ===== RESPONSE =====
        return AttendanceResponse.builder()
                .attendanceId(attendance.getId())
                .userId(user.getId())
                .fullName(user.getFullName())
                .role(user.getRole())
                .assignId(assign.getId())
                .shiftName(assign.getShift().getShiftName())
                .checkIn(now)
                .lateMinutes(lateMinutes)
                .status(status)
                .message("CHECK_IN_SUCCESS")
                .build();
    }

    // === CHECK_OUT ==============================================
    @Transactional
    public String checkOut(Integer assignmentId, Integer userId) {

        // ===== FIND ASSIGNMENT =====
        AssignStaff assign = assignStaffRepository
                .findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("SHIFT_ASSIGNMENT_NOT_FOUND"));

        // ===== CHECK OWNER =====
        if (!assign.getUser().getId().equals(userId)) {
            throw new RuntimeException("YOU_ARE_NOT_ALLOWED");
        }

        // ===== FIND ATTENDANCE =====
        Attendance attendance = attendanceRepository
                .findByShiftAssignmentId(assignmentId)
                .orElseThrow(() -> new RuntimeException("YOU_HAVE_NOT_CHECKED_IN"));

        // ===== MUST HAVE CHECK-IN =====
        if (attendance.getCheckIn() == null) {
            throw new RuntimeException("YOU_HAVE_NOT_CHECKED_IN");
        }

        // ===== ALREADY CHECKED OUT =====
        if (attendance.getCheckOut() != null) {
            throw new RuntimeException("ALREADY_CHECKED_OUT");
        }

        LocalDateTime now = LocalDateTime.now();

        // ===== SHIFT END TIME =====
        LocalDateTime shiftEndDateTime = LocalDateTime.of(
                assign.getWorkDate(),
                assign.getShift().getEndTime()
        );

        // ===== ONLY ALLOW CHECK-OUT AFTER SHIFT END =====
        if (now.isBefore(shiftEndDateTime)) {
            throw new RuntimeException("CANNOT_CHECK_OUT_BEFORE_SHIFT_END");
        }

        // ===== CHECK OUT =====
        attendance.setCheckOut(now);

        // ===== CALCULATE WORK HOURS =====
        Duration duration = Duration.between(
                attendance.getCheckIn(),
                attendance.getCheckOut()
        );

        double hours = duration.toMinutes() / 60.0;

        attendance.setWorkHours(
                BigDecimal.valueOf(hours)
                        .setScale(2, RoundingMode.HALF_UP)
        );

        // ===== EARLY LEAVE =====
        attendance.setEarlyLeaveMinutes(0);

        attendanceRepository.save(attendance);

        return "CHECK_OUT_SUCCESS";
    }

    // === VIEW LIST ATTENDANCE ==============================================
    @Transactional(readOnly = true)
    public PageResponse<AttendanceResponse> getMyAttendance(
            Integer userId,
            PaginationRequest request
    ) {

        Pageable pageable = PaginationUtil.build(request);

        Page<Attendance> attendancePage =
                attendanceRepository.findByUserId(userId, pageable);

        List<AttendanceResponse> content =
                attendancePage.getContent()
                        .stream()
                        .map(this::mapToAttendanceResponse)
                        .toList();

        return PageResponse.<AttendanceResponse>builder()
                .content(content)
                .page(attendancePage.getNumber())
                .size(attendancePage.getSize())
                .totalElements(attendancePage.getTotalElements())
                .totalPages(attendancePage.getTotalPages())
                .last(attendancePage.isLast())
                .build();
    }

    private AttendanceResponse mapToAttendanceResponse(Attendance attendance) {
        AssignStaff assign = attendance.getShiftAssignment();
        User user = attendance.getUser();
        String message;
        switch (attendance.getStatus()) {
            case "LATE":
                message = "You checked in late";
                break;

            case "ABSENT":
                message = "You were absent";
                break;

            case "LEAVE":
                message = "You were on leave";
                break;

            default:
                message = "Checked in successfully";
        }

        return AttendanceResponse.builder()
                .attendanceId(attendance.getId())
                .userId(user.getId())
                .fullName(user.getFullName())
                .role(user.getRole())
                .assignId(assign.getId())
                .shiftName(assign.getShift().getShiftName())
                .workDate(assign.getWorkDate())
                .checkIn(attendance.getCheckIn())
                .lateMinutes(attendance.getLateMinutes())
                .status(attendance.getStatus())
                .message(message)
                .build();
    }

    // === ADMIN VIEW LIST ATTENDANCE ==============================================
    @Transactional(readOnly = true)
    public PageResponse<AttendanceResponse> getAttendanceList(
            AttendanceFilterRequest request
    ) {

        Pageable pageable = PaginationUtil.build(request);

        Specification<Attendance> specification =
                AttendanceSpecification.filterAttendance(
                        request.getWorkDate(),
                        request.getMonth(),
                        request.getYear(),
                        request.getUserId(),
                        request.getShiftId()
                );

        Page<Attendance> attendancePage = attendanceRepository.findAll(specification, pageable);

        List<AttendanceResponse> content =
                attendancePage.getContent()
                        .stream()
                        .map(this::mapToAttendanceResponse)
                        .toList();

        return PageResponse.<AttendanceResponse>builder()
                .content(content)
                .page(attendancePage.getNumber())
                .size(attendancePage.getSize())
                .totalElements(attendancePage.getTotalElements())
                .totalPages(attendancePage.getTotalPages())
                .last(attendancePage.isLast())
                .build();
    }

}
