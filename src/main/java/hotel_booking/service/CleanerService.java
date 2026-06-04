package hotel_booking.service;

import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.request.DamageReportRequest;
import hotel_booking.dto.response.CleanerRoomResponse;
import hotel_booking.dto.response.DamageItemResponse;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.entity.Booking;
import hotel_booking.entity.CustomerNotification;
import hotel_booking.entity.Room;
import hotel_booking.entity.RoomSchedule;
import hotel_booking.entity.RoomDamage;
import hotel_booking.entity.BaseItem;
import hotel_booking.entity.RoomTypeItem;
import hotel_booking.repository.BookingRepository;
import hotel_booking.repository.CustomerNotificationRepository;
import hotel_booking.repository.RoomRepository;
import hotel_booking.repository.RoomScheduleRepository;
import hotel_booking.repository.RoomTypeItemRepository;
import hotel_booking.repository.RoomDamageRepository;
import hotel_booking.repository.CleanerNotificationRepository;
import hotel_booking.dto.response.CleanerNotificationResponse;
import hotel_booking.entity.CleanerNotification;
import hotel_booking.util.BookingPaginationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CleanerService {

    private final RoomScheduleRepository roomScheduleRepository;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final CustomerNotificationRepository notificationRepository;
    private final RoomTypeItemRepository roomTypeItemRepository;
    private final RoomDamageRepository roomDamageRepository;
    private final CleanerNotificationRepository cleanerNotificationRepository;

    // Get list cong viec dong phòng
    public PageResponse<CleanerRoomResponse> getRoomsNeedCleaning(PaginationRequest request) {

        Pageable pageable = BookingPaginationUtil.build(request);

        Page<RoomSchedule> page = roomScheduleRepository.findRoomsNeedCleaning(pageable);

        List<CleanerRoomResponse> content = page.getContent()
                .stream()
                .map(rs -> {
                    boolean hasDamage = roomDamageRepository.existsByBookingId(rs.getBooking().getId());
                    return CleanerRoomResponse.builder()
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
                        .bookingStatus(rs.getBooking().getStatus())
                        .hasDamageReport(hasDamage)
                        .build();
                })
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

        if (!"CHECKED_DAMAGE_ROOM".equals(booking.getStatus()) && !"CHECKED_OUT".equals(booking.getStatus())) {
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
        boolean alreadyCheckedOut = "CHECKED_OUT".equals(booking.getStatus());
        booking.setStatus("CHECKED_OUT");
        booking.setUpdatedAt(now);
        bookingRepository.save(booking);

        // =========================
        // NOTIFICATION
        // =========================
        if (!alreadyCheckedOut) {
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
        }

        return "CLEANING_COMPLETED_BY_BOOKING";
    }

    // =====================================================
    // GET DAMAGE ITEMS FOR BOOKING
    // =====================================================
    public List<DamageItemResponse> getDamageItemsForBooking(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("BOOKING_NOT_FOUND"));

        List<RoomSchedule> schedules = roomScheduleRepository.findByBooking_Id(bookingId);
        if (schedules.isEmpty()) {
            throw new RuntimeException("ROOM_SCHEDULE_NOT_FOUND");
        }

        // Lấy roomTypeId từ phòng vật lý đầu tiên trong lịch trình
        Integer roomTypeId = schedules.get(0).getRoom().getRoomType().getId();

        List<RoomTypeItem> typeItems = roomTypeItemRepository.findByRoomTypeId(roomTypeId);

        return typeItems.stream()
                .map(rti -> DamageItemResponse.builder()
                        .itemId(rti.getItem().getId())
                        .itemName(rti.getItem().getItemName())
                        .quantity(rti.getQuantity())
                        .baseUnitPrice(rti.getItem().getBaseUnitPrice())
                        .build()
                )
                .toList();
    }

    // =====================================================
    // SUBMIT DAMAGE REPORT
    // =====================================================
    @Transactional
    public String submitDamageReport(Integer bookingId, DamageReportRequest request) {
        // 1. Validate Booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("BOOKING_NOT_FOUND"));

        if (!"CHECKED_IN".equals(booking.getStatus()) && !"CHECKED_DAMAGE_ROOM".equals(booking.getStatus()) && !"CHECKED_OUT".equals(booking.getStatus())) {
            throw new RuntimeException("INVALID_BOOKING_STATUS");
        }

        // 2. Lấy RoomSchedule
        List<RoomSchedule> schedules = roomScheduleRepository.findByBooking_Id(bookingId);
        if (schedules.isEmpty()) {
            throw new RuntimeException("ROOM_SCHEDULE_NOT_FOUND");
        }
        Integer roomTypeId = schedules.get(0).getRoom().getRoomType().getId();

        List<RoomDamage> damagesToSave = new ArrayList<>();
        BigDecimal totalDamageFee = BigDecimal.ZERO;

        if (request.getDamages() != null && !request.getDamages().isEmpty()) {
            for (DamageReportRequest.DamageItem itemReq : request.getDamages()) {
                // Validate item belongs to room type
                RoomTypeItem rti = roomTypeItemRepository.findByRoomTypeIdAndItemId(roomTypeId, itemReq.getItemId())
                        .orElseThrow(() -> new RuntimeException("ITEM_NOT_FOUND_IN_ROOM_TYPE"));

                // Validate quantity
                if (itemReq.getQuantity() > rti.getQuantity()) {
                    throw new RuntimeException("REPORTED_QUANTITY_EXCEEDS_MAX");
                }

                // Validate Cloudinary URL
                if (itemReq.getEvidenceImageUrl() != null && !itemReq.getEvidenceImageUrl().isBlank()) {
                    if (!itemReq.getEvidenceImageUrl().contains("cloudinary.com")) {
                        throw new RuntimeException("INVALID_IMAGE_URL_MUST_BE_CLOUDINARY");
                    }
                }

                // Save to RoomDamage
                BaseItem baseItem = rti.getItem();
                RoomDamage damage = RoomDamage.builder()
                        .booking(booking)
                        .item(baseItem)
                        .quantity(itemReq.getQuantity())
                        .actualDamageFee(itemReq.getActualDamageFee())
                        .note(itemReq.getNote())
                        .evidenceImageUrl(itemReq.getEvidenceImageUrl())
                        .reportedAt(LocalDateTime.now())
                        .build();

                damagesToSave.add(damage);
                totalDamageFee = totalDamageFee.add(itemReq.getActualDamageFee());
            }
        }

        // Save damages
        if (!damagesToSave.isEmpty()) {
            roomDamageRepository.saveAll(damagesToSave);
        }

        // Transition Booking status
        if (totalDamageFee.compareTo(BigDecimal.ZERO) > 0) {
            // Có damage: Chờ thanh toán, trạng thái đặt phòng đổi thành/giữ là CHECKED_DAMAGE_ROOM
            booking.setStatus("CHECKED_DAMAGE_ROOM");
        } else {
            // Không có damage: Booking lập tức chuyển sang CHECKED_OUT
            booking.setStatus("CHECKED_OUT");
            
            // Đồng bộ trạng thái Room khi không có damage: 
            // Nếu booking được check-out thẳng, ta cũng cập nhật các Room tương ứng sang DIRTY để dọn dẹp
            for (RoomSchedule rs : schedules) {
                Room room = rs.getRoom();
                if ("READY".equals(room.getStatus()) || room.getStatus() == null) {
                    room.setStatus("DIRTY");
                }
                room.setUpdatedAt(LocalDateTime.now());
                roomRepository.save(room);
            }
        }

        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        return "DAMAGE_REPORT_SUBMITTED";
    }

    // =====================================================
    // GET CLEANER NOTIFICATIONS
    // =====================================================
    public List<CleanerNotificationResponse> getNotifications() {
        List<CleanerNotification> notifications = cleanerNotificationRepository.findAllByOrderByCreatedAtDesc();
        return notifications.stream()
                .map(n -> {
                    String roomNumber = "N/A";
                    if (n.getBooking() != null) {
                        List<RoomSchedule> schedules = roomScheduleRepository.findByBooking_Id(n.getBooking().getId());
                        if (!schedules.isEmpty()) {
                            roomNumber = schedules.get(0).getRoom().getRoomNumber();
                        }
                    }
                    return CleanerNotificationResponse.builder()
                            .id(n.getId())
                            .bookingId(n.getBooking() != null ? n.getBooking().getId() : null)
                            .title(n.getTitle())
                            .message(n.getMessage())
                            .isRead(n.getIsRead())
                            .createdAt(n.getCreatedAt())
                            .roomNumber(roomNumber)
                            .build();
                })
                .toList();
    }

    // =====================================================
    // MARK NOTIFICATION AS READ
    // =====================================================
    @Transactional
    public String markNotificationAsRead(Integer id) {
        CleanerNotification noti = cleanerNotificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NOTIFICATION_NOT_FOUND"));
        noti.setIsRead(true);
        cleanerNotificationRepository.save(noti);
        return "SUCCESS";
    }
}
