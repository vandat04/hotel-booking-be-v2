package hotel_booking.security;

import hotel_booking.repository.BookingRepository;
import hotel_booking.repository.RoomScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingAutoCancel {

    private final BookingRepository bookingRepository;
    private final RoomScheduleRepository roomScheduleRepository;

    @Transactional
    @Scheduled(fixedRate = 60000) // chạy mỗi 60s
    public void autoCancelUnpaidBookings() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime limit = now.minusMinutes(1);

        // 1. cancel booking
        int bookingUpdated = bookingRepository.cancelUnpaidBookings(limit);

        // 2. cancel room schedules
        int scheduleUpdated = roomScheduleRepository.cancelRoomSchedules(limit);

        System.out.println("[AUTO-CANCEL] bookings=" + bookingUpdated
                + ", schedules=" + scheduleUpdated);
    }
}
