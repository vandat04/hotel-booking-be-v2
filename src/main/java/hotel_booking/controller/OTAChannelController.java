package hotel_booking.controller;

import hotel_booking.dto.request.*;
import hotel_booking.dto.response.ApiResponse;
import hotel_booking.dto.response.OTAChannelResponse;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.service.OTAChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/ota-channels")
@RequiredArgsConstructor
public class OTAChannelController {

    private final OTAChannelService otaChannelService;

    // ================= CREATE OTA =================
    @PostMapping
    public ResponseEntity<ApiResponse<OTAChannelResponse>> create(
            @Valid @RequestBody CreateOTAChannelRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("OTA Channel created successfully", otaChannelService.create(request)));
    }

    // ================= VIEW OTA LIST =================
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OTAChannelResponse>>> getAll(
            @ModelAttribute FilterOTAChannelRequest request,
            @ModelAttribute PaginationRequest pagination
    ) {
        return ResponseEntity.ok(ApiResponse.success(otaChannelService.getAll(request, pagination)));
    }

    // ================= VIEW OTA LIST =================
    @PutMapping("/{otaId}")
    public ResponseEntity<ApiResponse<OTAChannelResponse>> update(
            @PathVariable Integer otaId,
            @Valid @RequestBody UpdateOTAChannelRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(otaChannelService.update(otaId, request)));
    }

    // ================= CREATE OTA BOOKING =================
    @PostMapping("/booking")
    public ResponseEntity<ApiResponse<String>> createBooking(
            @RequestBody AgodaBookingWebhookRequest request
    ) {
        otaChannelService.createBookingFromOTA(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("OTA booking created successfully", "OTA_BOOKING_CREATED"));
    }
}

