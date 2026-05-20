package hotel_booking.controller;

import hotel_booking.dto.request.CreateShiftRequest;
import hotel_booking.dto.request.UpdateShiftRequest;
import hotel_booking.dto.response.ShiftResponse;
import hotel_booking.service.ShiftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/shifts")
public class AdminShiftController {

    private final ShiftService shiftService;

    // ================= CREATE NEW SHIFTS =================
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShiftResponse createShift(
            @Valid @RequestBody CreateShiftRequest request
    ) {
        return shiftService.createShift(request);
    }

    // ================= UPDATE SHIFTS =================
    @PutMapping("/{shiftId}")
    public ShiftResponse updateShift(
            @PathVariable Integer shiftId,
            @Valid @RequestBody UpdateShiftRequest request
    ) {
        return shiftService.updateShift(shiftId, request);
    }

    // =================== GET LIST SHIFT ==================================
    @GetMapping
    public List<ShiftResponse> getAllShifts(
            @RequestParam(required = false)
            Boolean isActive
    ) {
        return shiftService.getAllShifts(isActive);
    }
}
