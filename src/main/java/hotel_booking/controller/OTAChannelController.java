package hotel_booking.controller;

import hotel_booking.dto.request.*;
import hotel_booking.dto.response.OTAChannelResponse;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.service.OTAChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/ota-channels")
@RequiredArgsConstructor
public class OTAChannelController {

    private final OTAChannelService otaChannelService;

    // ================= CREATE OTA =================
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OTAChannelResponse create(
            @Valid @RequestBody CreateOTAChannelRequest request
    ) {
        return otaChannelService.create(request);
    }

    // ================= VIEW OTA LIST =================
    @GetMapping
    public PageResponse<OTAChannelResponse> getAll(
            @ModelAttribute FilterOTAChannelRequest request,
            @ModelAttribute PaginationRequest pagination
    ) {
        return otaChannelService.getAll(request, pagination);
    }

    // ================= VIEW OTA LIST =================
    @PutMapping("/{otaId}")
    public OTAChannelResponse update(
            @PathVariable Integer otaId,
            @Valid @RequestBody UpdateOTAChannelRequest request
    ) {
        return otaChannelService.update(otaId, request);
    }

    // ================= CREATE OTA BOOKING =================
    @PostMapping("/booking")
    @ResponseStatus(HttpStatus.CREATED)
    public String createBooking(
            @RequestBody AgodaBookingWebhookRequest request
    ) {
        otaChannelService.createBookingFromOTA(request);
        return "OTA_BOOKING_CREATED";
    }
}
