package hotel_booking.service;


import hotel_booking.dto.request.*;
import hotel_booking.dto.response.CheckAvailabilityResponse;
import hotel_booking.dto.response.OTAChannelResponse;
import hotel_booking.dto.response.OTADashboardResponse;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.entity.*;
import hotel_booking.repository.*;
import hotel_booking.util.BookingPaginationUtil;
import hotel_booking.util.PaginationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OTAChannelService {

    private final OTAChannelRepository otaChannelRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final RoomScheduleRepository roomScheduleRepository;
    private final NotificationService notificationService;

    // ==================================
    // ======= CREATE OTA  ========
    // ==================================
    public OTAChannelResponse create(CreateOTAChannelRequest request) {
        // ===== CHECK EXIST =====
        if (otaChannelRepository.existsByName(request.getName())) {
            throw new RuntimeException("OTA_CHANNEL_ALREADY_EXISTS");
        }
        // ===== CREATE ENTITY =====
        OTAChannel otaChannel = OTAChannel.builder()
                .otaHotelId("1")
                .name(request.getName().trim().toUpperCase())
                .apiKeySecret(request.getApiKeySecret())
                .webhookSecret(request.getWebhookSecret())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
        // ===== SAVE =====
        otaChannelRepository.save(otaChannel);
        // ===== RESPONSE =====
        return toResponse(otaChannel);
    }

    private OTAChannelResponse toResponse(OTAChannel ota) {
        return OTAChannelResponse.builder()
                .id(ota.getId())
                .name(ota.getName())
                .apiKeySecret(ota.getApiKeySecret())
                .webhookSecret(ota.getWebhookSecret())
                .isActive(ota.getIsActive())
                .build();
    }

    // ==================================
    // ======= VIEW LIST OTA  ========
    // ==================================
    public PageResponse<OTAChannelResponse> getAll(
            FilterOTAChannelRequest request,
            PaginationRequest pagination
    ) {
        Pageable pageable = PaginationUtil.build(pagination);
        Page<OTAChannel> otaPage = otaChannelRepository.filterOTAChannels(request.getKeyword(), request.getIsActive(), pageable);

        List<OTAChannelResponse> content =
                otaPage.getContent()
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return PageResponse.<OTAChannelResponse>builder()
                .content(content)
                .page(otaPage.getNumber())
                .size(otaPage.getSize())
                .totalElements(otaPage.getTotalElements())
                .totalPages(otaPage.getTotalPages())
                .last(otaPage.isLast())
                .build();
    }

    // ==================================
    // ======= UPDATE OTA  ========
    // ==================================
    @Transactional
    public OTAChannelResponse update(
            Integer otaId,
            UpdateOTAChannelRequest request
    ) {

        // ===== FIND OTA =====
        OTAChannel ota = otaChannelRepository.findById(otaId)
                .orElseThrow(() -> new RuntimeException("OTA_CHANNEL_NOT_FOUND"));

        // ===== CHECK DUPLICATE NAME =====
        if (otaChannelRepository.existsByNameAndIdNot(request.getName().trim().toUpperCase(), otaId)) {
            throw new RuntimeException("OTA_CHANNEL_NAME_ALREADY_EXISTS");
        }

        // ===== UPDATE =====
        ota.setName(request.getName().trim().toUpperCase());
        ota.setApiKeySecret(request.getApiKeySecret());
        ota.setWebhookSecret(request.getWebhookSecret());

        if (request.getIsActive() != null) {
            ota.setIsActive(request.getIsActive());
        }

        // ===== SAVE =====
        otaChannelRepository.save(ota);

        return toResponse(ota);
    }

    // ==================================
    // ======= CREATE OTA BOOKING  ========
    // ==================================
    @Transactional
    public void createBookingFromOTA(
            AgodaBookingWebhookRequest request
    ) {

        // ===== FIND ROOM TYPE =====
        RoomType roomType = roomTypeRepository.findByName(request.getRoom().getRoomTypeCode())
                .orElseThrow(() -> new RuntimeException("ROOM_TYPE_NOT_FOUND"));

        // ===== CHECK AVAILABILITY =====
        CheckAvailabilityRequest availabilityRequest =
                CheckAvailabilityRequest.builder()
                        .roomTypeId(roomType.getId())
                        .checkIn(request.getStay().getCheckIn())
                        .checkOut(request.getStay().getCheckOut())
                        .numberOfRoom(request.getRoom().getQuantity())
                        .bookingType("DAILY")
                        .build();

        CheckAvailabilityResponse availability = bookingService.checkAvailability(availabilityRequest);

        // ===== VALIDATE =====
        if (!availability.getAvailable()) {
            throw new RuntimeException(availability.getMessage());
        }

        // ===== CREATE BOOKING =====
        Booking booking = Booking.builder()
                .customer(null)
                .customerName(request.getGuest().getFullName())
                .customerPhone(request.getGuest().getPhone())
                .customerEmail(request.getGuest().getEmail())
                .roomType(roomType)
                .requestedQuantity(request.getRoom().getQuantity())
                .requestedCheckin(request.getStay().getCheckIn())
                .requestedCheckout(request.getStay().getCheckOut())
                .bookingType("DAILY")
                .bookingSource(request.getOtaChannel())
                .status(request.getStatus())
                .paymentStatus(request.getPayment().getPaymentStatus())
                .totalAmount(availability.getTotalAmount())
                .notes(buildNotes(request))
                .createdAt(LocalDateTime.now())

                .build();

        bookingRepository.save(booking);

        // ===== CREATE ROOM SCHEDULE =====
        for (Integer roomId : availability.getListRoomCanBook()) {
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new RuntimeException("ROOM_NOT_FOUND"));

            RoomSchedule schedule =
                    RoomSchedule.builder()
                            .booking(booking)
                            .room(room)
                            .startAt(request.getStay().getCheckIn())
                            .endAt(request.getStay().getCheckOut())
                            .status("SCHEDULED")
                            .createdAt(LocalDateTime.now())
                            .build();
            roomScheduleRepository.save(schedule);
        }

        notificationService.createCustomerNotification(null, booking, "BOOKING ROOM IN CHECK-X", "Booking Success From OTA", "PAYMENT_SUCCESS");

    }

    private String buildNotes(
            AgodaBookingWebhookRequest request
    ) {
        StringBuilder notes = new StringBuilder();
        notes.append("OTA Booking ID: ").append(request.getBookingId());
        notes.append("\nOTA Hotel ID: ").append(request.getOtaHotelId());
        notes.append("\nOTA Channel: ").append(request.getOtaChannel());
        notes.append("\nPayment Type: ").append(request.getPayment().getPaymentType());
        notes.append("\nCurrency: ").append(request.getPricing().getCurrency());
        notes.append("\nCommission Amount: ").append(request.getPricing().getCommissionAmount());

        if (request.getSpecialRequests() != null && !request.getSpecialRequests().isEmpty()) {
            notes.append("\nSpecial Requests:");
            request.getSpecialRequests().forEach(item -> notes.append("\n- ").append(item));
        }

        return notes.toString();
    }

    // ==================================
    // ======= DASHBOARD OTA BOOKING  ========
    // ==================================
    public OTADashboardResponse getDashboard() {
        long totalChannels = otaChannelRepository.count();
        long activeChannels = otaChannelRepository.countByIsActiveTrue();
        long inactiveChannels = otaChannelRepository.countByIsActiveFalse();
        return OTADashboardResponse.builder()
                .totalChannels(totalChannels)
                .activeChannels(activeChannels)
                .inactiveChannels(inactiveChannels)
                .build();
    }
}