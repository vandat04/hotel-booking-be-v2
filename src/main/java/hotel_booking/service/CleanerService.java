package hotel_booking.service;

import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.response.CleanerRoomResponse;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.entity.Booking;
import hotel_booking.entity.CustomerNotification;
import hotel_booking.entity.Room;
import hotel_booking.entity.RoomSchedule;
import hotel_booking.repository.BookingRepository;
import hotel_booking.repository.CustomerNotificationRepository;
import hotel_booking.repository.RoomRepository;
import hotel_booking.repository.RoomScheduleRepository;
import hotel_booking.util.BookingPaginationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CleanerService {

    private final RoomScheduleRepository roomScheduleRepository;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final CustomerNotificationRepository notificationRepository;

    // Get list cong viec dong phòng
    public PageResponse<CleanerRoomResponse> getRoomsNeedCleaning(PaginationRequest request) {

        Pageable pageable = BookingPaginationUtil.build(request);

        Page<RoomSchedule> page = roomScheduleRepository.findRoomsNeedCleaning(pageable);

        List<CleanerRoomResponse> content = page.getContent()
                .stream()
                .map(rs -> CleanerRoomResponse.builder()
                        .bookingId(rs.getBooking().getId())
                        .roomId(rs.getRoom().getId())
                        .roomNumber(rs.getRoom().getRoomNumber())
                        .roomTypeName(rs.getRoom().getRoomType().getName())

                        .customerName(rs.getBooking().getCustomerName())
                        .customerPhone(rs.getBooking().getCustomerPhone())

                        .checkin(rs.getStartAt())
                        .checkout(rs.getEndAt())

                        .roomStatus(rs.getRoom().getStatus())
                        .scheduleStatus(rs.getStatus())
                        .build()
                )
                .toList();

        return PageResponse.<CleanerRoomResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }


    // =====================================================
    // DONE CLEAN TASK
    // =====================================================
    @Transactional
    public String completeCleaningByBooking(Integer bookingId) {

        // 1. Lấy booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("BOOKING_NOT_FOUND"));

        if (!"CHECKED_DAMAGE_ROOM".equals(booking.getStatus())) {
            throw new RuntimeException("INVALID_BOOKING_STATUS");
        }

        // 2. Lấy tất cả room schedules theo booking
        List<RoomSchedule> schedules = roomScheduleRepository.findByBooking_Id(bookingId);
        if (schedules.isEmpty()) {
            throw new RuntimeException("NO_ROOM_SCHEDULE_FOUND");
        }

        LocalDateTime now = LocalDateTime.now();

        // =========================
        // UPDATE ROOM SCHEDULE + ROOM
        // =========================
        for (RoomSchedule rs : schedules) {

            // 1. update schedule
            rs.setStatus("COMPLETED");
            rs.setUpdatedAt(now);

            // 2. update room
            Room room = rs.getRoom();

            // chỉ chuyển READY nếu không bị maintenance
            if (!"MAINTENANCE".equals(room.getStatus())) {
                room.setStatus("READY");
            }

            room.setUpdatedAt(now);

            roomRepository.save(room);
        }

        roomScheduleRepository.saveAll(schedules);

        // =========================
        // UPDATE BOOKING
        // =========================
        booking.setStatus("CHECKED_OUT");
        booking.setUpdatedAt(now);
        bookingRepository.save(booking);

        // =========================
        // NOTIFICATION
        // =========================
        CustomerNotification noti = new CustomerNotification();
        noti.setUser(booking.getCustomer());
        noti.setBooking(booking);
        noti.setTitle("BOOKING ROOM IN CHECK-X");
        noti.setMessage("Check-out successful");
        noti.setNotificationType("BOOKING_SUCCESS");
        noti.setSentViaEmail(false);
        noti.setSentViaAppPush(true);
        noti.setIsRead(false);
        noti.setCreatedAt(now);

        notificationRepository.save(noti);

        return "CLEANING_COMPLETED_BY_BOOKING";
    }
}
