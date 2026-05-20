package hotel_booking.controller;

import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.request.SalaryFilterRequest;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.dto.response.SalarySheetResponse;
import hotel_booking.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/salary")
@RequiredArgsConstructor
public class AdminSalaryController {

    private final SalaryService salaryService;

    @PostMapping("/calculate-all")
    public ResponseEntity<List<SalarySheetResponse>> calculateSalaryForAll(
            @RequestParam Integer month,
            @RequestParam Integer year
    ) {
        return ResponseEntity.ok(
                salaryService.calculateMonthlySalaryForAllStaff(
                        month,
                        year
                )
        );
    }

    @GetMapping
    public ResponseEntity<PageResponse<SalarySheetResponse>>
    getSalarySheets(
            SalaryFilterRequest filter,
            PaginationRequest pagination
    ) {
        return ResponseEntity.ok(salaryService.getSalarySheets(filter, pagination));
    }

    @PutMapping("/{salarySheetId}/paid")
    public ResponseEntity<SalarySheetResponse> updateSalaryStatusPaid(
            @PathVariable Integer salarySheetId
    ) {
        return ResponseEntity.ok(salaryService.updateSalaryStatusPaid(salarySheetId));
    }
}