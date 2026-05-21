package hotel_booking.controller;

import hotel_booking.dto.request.CreateShiftRequest;
import hotel_booking.dto.request.UpdateShiftRequest;
import hotel_booking.dto.response.ApiResponse;
import hotel_booking.dto.response.ShiftResponse;
import hotel_booking.service.ShiftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/shifts")
public class AdminShiftController {

    private final ShiftService shiftService;

    // ================= CREATE NEW SHIFTS =================
    @PostMapping
    public ResponseEntity<ApiResponse<ShiftResponse>> createShift(
            @Valid @RequestBody CreateShiftRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Shift created successfully", shiftService.createShift(request)));
    }

    // ================= UPDATE SHIFTS =================
    @PutMapping("/{shiftId}")
    public ResponseEntity<ApiResponse<ShiftResponse>> updateShift(
            @PathVariable Integer shiftId,
            @Valid @RequestBody UpdateShiftRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(shiftService.updateShift(shiftId, request)));
    }

    // =================== GET LIST SHIFT ==================================
    @GetMapping
    public ResponseEntity<ApiResponse<List<ShiftResponse>>> getAllShifts(
            @RequestParam(required = false) Boolean isActive
    ) {
        return ResponseEntity.ok(ApiResponse.success(shiftService.getAllShifts(isActive)));
    }
}
