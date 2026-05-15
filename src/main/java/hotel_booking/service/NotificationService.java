package hotel_booking.service;

import hotel_booking.entity.Booking;
import hotel_booking.entity.CustomerNotification;
import hotel_booking.entity.User;
import hotel_booking.repository.CustomerNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final CustomerNotificationRepository notificationRepository;
    private final EmailService emailService;

    // ================= CREATE NOTIFICATION =================
    public void createCustomerNotification(
            User user,
            Booking booking,
            String title,
            String message,
            String type
    ) {
        //=== GUI MAIL
        emailService.sendCustomerEmail(user.getEmail(), title, message);
        //=== GUI THONG BAO
        CustomerNotification notification = CustomerNotification.builder()
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
}
