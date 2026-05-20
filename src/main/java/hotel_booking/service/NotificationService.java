package hotel_booking.service;

import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.response.CustomerNotificationResponse;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.entity.Booking;
import hotel_booking.entity.CustomerNotification;
import hotel_booking.entity.User;
import hotel_booking.repository.CustomerNotificationRepository;
import hotel_booking.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final CustomerNotificationRepository notificationRepository;
    private final EmailService emailService;

    // ================= CREATE CUSTOMER NOTIFICATION =================
    public void createCustomerNotification(
            User user,
            Booking booking,
            String title,
            String message,
            String type
    ) {

        // ================= HANDLE GUEST =================
        if (user != null && user.getId() == null) {
            user = null;
        }

        // ================= SAVE NOTIFICATION =================
        CustomerNotification notification =
                CustomerNotification.builder()
                        .user(user)
                        .booking(booking)
                        .title(title)
                        .message(message)
                        .notificationType(type)
                        .sentViaEmail(true)
                        .sentViaAppPush(false)
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .build();

        notificationRepository.save(notification);
    }

    // ================= VIEW CUSTOMER NOTIFICATION =================
    public Page<CustomerNotificationResponse> getCustomerNotifications(
            Integer userId,
            PaginationRequest request
    ) {
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<CustomerNotification> notificationPage =
                notificationRepository.findByUser_Id(userId, pageable);
        return notificationPage.map(this::mapToResponse);
    }

    private CustomerNotificationResponse mapToResponse(
            CustomerNotification notification
    ) {
        return CustomerNotificationResponse.builder()
                .id(notification.getId())
                .bookingId(notification.getBooking() != null ? notification.getBooking().getId() : null)
                .title(notification.getTitle())
                .message(notification.getMessage())
                .notificationType(notification.getNotificationType())
                .isRead(notification.getIsRead())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    // ================= VIEW ALL NOTIFICATION =================
    public PageResponse<CustomerNotificationResponse> getAll(PaginationRequest request) {

        // mặc định sort theo mới nhất
        if (request.getSortBy() == null || request.getSortBy().isBlank()) {
            request.setSortBy("createdAt");
            request.setDirection("desc");
        }

        Pageable pageable = PaginationUtil.build(request);

        Page<CustomerNotification> page = notificationRepository.findAll(pageable);

        List<CustomerNotificationResponse> content = page.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return PageResponse.<CustomerNotificationResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

}
