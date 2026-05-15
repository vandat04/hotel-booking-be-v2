package hotel_booking.service;

import hotel_booking.dto.request.CheckAvailabilityRequest;
import hotel_booking.dto.request.CreateBookingRequest;
import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.response.BookingHistoryResponse;
import hotel_booking.dto.response.BookingResponse;
import hotel_booking.dto.response.CheckAvailabilityResponse;
import hotel_booking.entity.*;
import hotel_booking.repository.*;
import hotel_booking.util.PaginationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomScheduleRepository roomScheduleRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public CheckAvailabilityResponse checkAvailability(CheckAvailabilityRequest req) {

        LocalDateTime now = LocalDateTime.now();

        // ==============================
        // 1. VALIDATE
        // ==============================
        if (req.getCheckIn() == null || req.getCheckOut() == null) {
            throw new IllegalArgumentException("Check-in / Check-out cannot be null");
        }

        if (req.getCheckIn().isBefore(now)) {
            throw new IllegalArgumentException("Check-in must be after current time");
        }

        if (!req.getCheckOut().isAfter(req.getCheckIn())) {
            throw new IllegalArgumentException("Check-out must be after check-in");
        }

        if (req.getNumberOfRoom() == null || req.getNumberOfRoom() <= 0) {
            throw new IllegalArgumentException("Number of rooms must be greater than 0");
        }

        String requestType = req.getBookingType().toUpperCase();

        // ==============================
        // 2. TOTAL ROOMS
        // ==============================
        int totalRooms = roomRepository.countTotalRooms(req.getRoomTypeId());

        // ==============================
        // 3. AVAILABLE ROOMS (RAW LIST)
        // ==============================
        List<Room> availableRoomsList =
                roomRepository.findAvailableRooms(
                        req.getRoomTypeId(),
                        req.getCheckIn(),
                        req.getCheckOut()
                );

        // ==============================
        // 4. SORT PRIORITY
        // ==============================
        Comparator<Room> comparator = Comparator
                // (1) ưu tiên theo booking type
                .comparing((Room r) -> {
                    if ("HOURLY".equals(requestType)) {
                        return "HOURLY".equals(r.getAllocatedFor()) ? 0 : 1;
                    } else {
                        return "DAILY".equals(r.getAllocatedFor()) ? 0 : 1;
                    }
                })

                // (2) ưu tiên phòng gần available nhất (expectedCheckoutAt)
                .thenComparing(r ->
                        r.getExpectedCheckoutAt() == null
                                ? LocalDateTime.MIN
                                : r.getExpectedCheckoutAt()
                )

                // (3) fallback: sort theo room id
                .thenComparing(Room::getId);

        availableRoomsList.sort(comparator);

        // ==============================
        // 5. PICK ROOMS
        // ==============================
        List<Integer> listRoomCanBook = availableRoomsList.stream()
                .limit(req.getNumberOfRoom())
                .map(Room::getId)
                .toList();

        int availableRoomsCount = availableRoomsList.size();
        boolean isAvailable = availableRoomsCount >= req.getNumberOfRoom();

        // ==============================
        // 6. PRICE
        // ==============================
        Duration duration = Duration.between(req.getCheckIn(), req.getCheckOut());

        long minutes = duration.toMinutes();
        long hours = Math.max(1, (long) Math.ceil(minutes / 60.0));
        long days = Math.max(1, (long) Math.ceil(minutes / 1440.0));

        BigDecimal pricePerDay = roomTypeRepository.findPricePerDayById(req.getRoomTypeId());
        BigDecimal pricePerHour = roomTypeRepository.findPricePerHourById(req.getRoomTypeId());

        BigDecimal totalAmount;

        if ("DAILY".equalsIgnoreCase(requestType)) {
            totalAmount = pricePerDay
                    .multiply(BigDecimal.valueOf(days))
                    .multiply(BigDecimal.valueOf(req.getNumberOfRoom()));
        } else {
            if (hours > 12) {
                throw new RuntimeException("The hotel only allows bookings of less than 12 hours if booked on a Hourly basis.");
            }

            totalAmount = pricePerHour
                    .multiply(BigDecimal.valueOf(hours))
                    .multiply(BigDecimal.valueOf(req.getNumberOfRoom()));
        }

        // ==============================
        // 7. RESPONSE
        // ==============================
        return CheckAvailabilityResponse.builder()
                .available(isAvailable)
                .totalRooms(totalRooms)
                .occupiedRooms(totalRooms - availableRoomsCount)
                .availableRooms(availableRoomsCount)
                .listRoomCanBook(listRoomCanBook)
                .totalAmount(totalAmount)
                .message(isAvailable
                        ? "Available"
                        : "Unavailable - only " + availableRoomsCount + " rooms left")
                .build();
    }

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest req) {

        // ================= RE-CHECK =================
        CheckAvailabilityResponse availability =
                checkAvailability(req.getAvailabilityRequest());

        if (!availability.getAvailable()) {
            throw new RuntimeException("Rooms just got booked by someone else");
        }

        List<Integer> roomIds = availability.getListRoomCanBook();

        if (roomIds.size() < req.getAvailabilityRequest().getNumberOfRoom()) {
            throw new RuntimeException("Not enough rooms available");
        }

        // ================= USER + ROOM TYPE =================
        User user = userRepository.findById(req.getCustomerId()).orElse(null);

        RoomType roomType = roomTypeRepository.findById(
                req.getAvailabilityRequest().getRoomTypeId()
        ).orElseThrow(() -> new RuntimeException("Room type not found"));

        // ================= CREATE BOOKING =================
        Booking booking = Booking.builder()
                .customer(user)
                .customerName(req.getCustomerName())
                .customerPhone(req.getCustomerPhone())
                .customerEmail(req.getCustomerEmail())

                .roomType(roomType)
                .requestedQuantity(req.getAvailabilityRequest().getNumberOfRoom())
                .requestedCheckin(req.getAvailabilityRequest().getCheckIn())
                .requestedCheckout(req.getAvailabilityRequest().getCheckOut())
                .bookingType(req.getAvailabilityRequest().getBookingType())
                .bookingSource(req.getBookingSource())
                .status("PENDING")
                .totalAmount(availability.getTotalAmount())
                .paymentStatus("UNPAID")
                .notes(req.getNotes())
                .createdAt(LocalDateTime.now())
                .build();

        booking = bookingRepository.save(booking);

        // ================= CREATE ROOM SCHEDULE =================
        Booking finalBooking = booking;
        List<RoomSchedule> schedules = roomIds.stream()
                .map(roomId -> RoomSchedule.builder()
                        .booking(finalBooking)
                        .room(roomRepository.getReferenceById(roomId))
                        .startAt(req.getAvailabilityRequest().getCheckIn())
                        .endAt(req.getAvailabilityRequest().getCheckOut())
                        .status("HOLD")
                        .createdAt(LocalDateTime.now())
                        .build()
                ).toList();

        roomScheduleRepository.saveAll(schedules);

        notificationService.createCustomerNotification(user, booking, "BOOKING ROOM IN CHECK-X", "Room reservation successful, please process your booking within 1 minute.","BOOKING_SUCCESS");

        return BookingResponse.builder()
                .bookingId(booking.getId())
                .status("PENDING")
                .message("Room reservation successful, please process your booking within 1 minute.")
                .build();
    }

    public Page<BookingHistoryResponse> getBookingHistory(
            Integer customerId,
            PaginationRequest req
    ) {

        Pageable pageable = PaginationUtil.build(req);

        List<String> statuses = List.of(
                "PENDING",
                "CONFIRMED",
                "CHECKED_IN",
                "CHECKED_OUT",
                "CANCELLED",
                "NO_SHOW"
        );

        Page<Booking> bookings = bookingRepository
                .findByCustomerIdAndStatusIn(customerId, statuses, pageable);

        return bookings.map(this::mapToHistoryResponse);
    }

    public Page<BookingHistoryResponse> getBookingHistory(
            Integer customerId,
            PaginationRequest req,
            String status
    ) {

        Pageable pageable = PaginationUtil.build(req);

        Page<Booking> bookings = bookingRepository
                .findByCustomerIdAndStatus(customerId, status, pageable);

        return bookings.map(this::mapToHistoryResponse);
    }

    private BookingHistoryResponse mapToHistoryResponse(Booking b) {

        return BookingHistoryResponse.builder()
                .bookingId(b.getId())
                .roomTypeName(b.getRoomType().getName())
                .quantity(b.getRequestedQuantity())
                .checkIn(b.getRequestedCheckin())
                .checkOut(b.getRequestedCheckout())
                .bookingType(b.getBookingType())
                .status(b.getStatus())
                .totalAmount(b.getTotalAmount())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
