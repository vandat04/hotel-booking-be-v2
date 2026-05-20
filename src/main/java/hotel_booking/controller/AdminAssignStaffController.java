package hotel_booking.controller;

import hotel_booking.dto.request.AssignStaffRequest;
import hotel_booking.dto.response.AssignStaffResponse;
import hotel_booking.service.AssignStaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/assign-staff")
@RequiredArgsConstructor
public class AdminAssignStaffController {

    private final AssignStaffService assignStaffService;

    // CHI DINH LICH LAM VIEC =====================================================
    @PostMapping
    public AssignStaffResponse assignStaff(
            @Valid @RequestBody AssignStaffRequest request
    ) {
        return assignStaffService.assignStaff(request);
    }

    // VIEW LIST LICH LAM VIEC TRONG TUAN =====================================================
    @GetMapping("/current-week")
    public List<AssignStaffResponse> getCurrentWeekAssignments() {
        return assignStaffService.getCurrentWeekAssignments();
    }

    // DELETE LICH LAM VIEC  =====================================================
    @DeleteMapping("/{assignId}")
    public String deleteAssignStaff(
            @PathVariable Integer assignId
    ) {
        return assignStaffService.deleteAssignStaff(assignId);
    }
}
