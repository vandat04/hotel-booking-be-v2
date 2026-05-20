package hotel_booking.service;

import hotel_booking.dto.request.CreateShiftRequest;
import hotel_booking.dto.request.UpdateShiftRequest;
import hotel_booking.dto.response.ShiftResponse;
import hotel_booking.entity.Shift;
import hotel_booking.repository.ShiftRepository;
import hotel_booking.service.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShiftService {
    private final ShiftRepository shiftRepository;

    // ======== CREATE NEW SHIFTS ========================
    public ShiftResponse createShift(CreateShiftRequest request) {
        // ===== VALIDATE SHIFT NAME =====
        String shiftName = request.getShiftName().trim();
        if (shiftRepository.existsByShiftNameIgnoreCase(shiftName)) {
            throw new RuntimeException("SHIFT_NAME_ALREADY_EXISTS");
        }

        // ===== VALIDATE TIME =====
        LocalTime startTime = request.getStartTime();
        LocalTime endTime = request.getEndTime();
        if (startTime.equals(endTime)) {
            throw new RuntimeException("SHIFT_TIME_INVALID");
        }

        // ===== VALIDATE DUPLICATE TIME =====
        int duplicateTime = shiftRepository.existsShiftTime(startTime.toString(), endTime.toString());
        if (duplicateTime > 0) {
            throw new RuntimeException("SHIFT_TIME_ALREADY_EXISTS");
        }

        // ===== CREATE SHIFT =====
        Shift shift = Shift.builder()
                .shiftName(shiftName)
                .startTime(startTime)
                .endTime(endTime)
                .description(request.getDescription())
                .isActive(true)
                .build();
        shiftRepository.save(shift);

        // ===== RESPONSE =====
        return ShiftResponse.builder()
                .id(shift.getId())
                .shiftName(shift.getShiftName())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .description(shift.getDescription())
                .isActive(shift.getIsActive())
                .build();
    }

    // ======== UPDATE SHIFTS ========================
    public ShiftResponse updateShift(
            Integer shiftId,
            UpdateShiftRequest request
    ) {

        // ===== FIND SHIFT =====
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new RuntimeException("SHIFT_NOT_FOUND"));

        // ===== VALIDATE SHIFT NAME =====
        String shiftName = request.getShiftName().trim();

        boolean existsName =
                shiftRepository.existsByShiftNameIgnoreCaseAndIdNot(
                        shiftName,
                        shiftId
                );

        if (existsName) {
            throw new RuntimeException("SHIFT_NAME_ALREADY_EXISTS");
        }

        // ===== VALIDATE TIME =====
        if (request.getStartTime().equals(request.getEndTime())) {
            throw new RuntimeException("SHIFT_TIME_INVALID");
        }

        // ===== UPDATE =====
        shift.setShiftName(shiftName);
        shift.setStartTime(request.getStartTime());
        shift.setEndTime(request.getEndTime());
        shift.setDescription(request.getDescription());
        shift.setIsActive(request.getIsActive());

        try {
            shiftRepository.save(shift);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("SHIFT_TIME_ALREADY_EXISTS");
        }

        // ===== RESPONSE =====
        return ShiftResponse.builder()
                .id(shift.getId())
                .shiftName(shift.getShiftName())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .description(shift.getDescription())
                .isActive(shift.getIsActive())
                .build();
    }

    // GET LIST SHIFT=====================================================
    public List<ShiftResponse> getAllShifts(Boolean isActive) {
        List<Shift> shifts;
        // nếu không truyền -> lấy tất cả
        if (isActive == null) {
            shifts = shiftRepository.findAllByOrderByIdDesc();
        }
        // lọc theo trạng thái
        else {
            shifts = shiftRepository.findByIsActiveOrderByIdDesc(isActive);
        }
        return shifts.stream().map(this::mapToResponse).toList();
    }

    // MAPPER =====================================================
    private ShiftResponse mapToResponse(Shift shift) {

        int hours = (int) Duration.between(
                shift.getStartTime(),
                shift.getEndTime()
        ).toHours();

        return ShiftResponse.builder()
                .id(shift.getId())
                .shiftName(shift.getShiftName())
                .startTime(shift.getStartTime())
                .endTime(shift.getEndTime())
                .description(shift.getDescription())
                .isActive(shift.getIsActive())
                .build();
    }
}
