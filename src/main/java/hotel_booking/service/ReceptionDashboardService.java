package hotel_booking.service;

import hotel_booking.dto.response.ReceptionDashboardResponse;
import hotel_booking.dto.response.UpcomingCheckInDTO;
import hotel_booking.entity.RoomSchedule;
import hotel_booking.repository.PaymentRepository;
import hotel_booking.repository.RoomRepository;
import hotel_booking.repository.RoomScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceptionDashboardService {
    private final RoomRepository roomRepository;
    private final RoomScheduleRepository scheduleRepository;
    private final PaymentRepository paymentRepository;

    public ReceptionDashboardResponse getTodayDashboard() {

        // 1. ROOM DATA
        int totalActiveRooms = roomRepository.countActiveRooms();
        int readyRooms = roomRepository.countReadyRooms();
        long occupiedRooms = scheduleRepository.countOccupiedRooms();

        double occupancyRate = totalActiveRooms == 0
                ? 0
                : BigDecimal.valueOf(((double) occupiedRooms / totalActiveRooms) * 100)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();

        // 2. REVENUE (Using database-independent Java time range parameters)
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        LocalDateTime yesterdayStart = todayStart.minusDays(1);
        LocalDateTime yesterdayEnd = todayEnd.minusDays(1);

        BigDecimal todayRevenue = paymentRepository.getRevenueBetween(todayStart, todayEnd);
        BigDecimal yesterdayRevenue = paymentRepository.getRevenueBetween(yesterdayStart, yesterdayEnd);

        double growthPercent = calculateGrowth(todayRevenue, yesterdayRevenue);

        // 3. UPCOMING CHECK-IN (From 2 hours ago for late arrivals, up to 1 hour in the future: checkIn < now + 1h)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusHours(2);
        LocalDateTime end = now.plusHours(1);

        List<RoomSchedule> schedules =
                scheduleRepository.findUpcomingCheckIns(start, end);

        List<UpcomingCheckInDTO> checkIns = schedules.stream()
                .map(rs -> {
                    Long bookingId = (rs.getBooking() != null && rs.getBooking().getId() != null)
                            ? rs.getBooking().getId().longValue()
                            : null;
                    String customerName = rs.getBooking() != null ? rs.getBooking().getCustomerName() : null;
                    String phone = rs.getBooking() != null ? rs.getBooking().getCustomerPhone() : null;
                    String roomTypeName = (rs.getBooking() != null && rs.getBooking().getRoomType() != null)
                            ? rs.getBooking().getRoomType().getName()
                            : null;
                    return new UpcomingCheckInDTO(
                            bookingId,
                            customerName,
                            phone,
                            rs.getStartAt(),
                            roomTypeName
                    );
                })
                .toList();

        return new ReceptionDashboardResponse(
                totalActiveRooms,
                occupiedRooms,
                readyRooms,
                occupancyRate,
                todayRevenue,
                yesterdayRevenue,
                growthPercent,
                checkIns
        );
    }

    private double calculateGrowth(BigDecimal today, BigDecimal yesterday) {
        BigDecimal todayVal = today != null ? today : BigDecimal.ZERO;
        BigDecimal yesterdayVal = yesterday != null ? yesterday : BigDecimal.ZERO;

        if (yesterdayVal.compareTo(BigDecimal.ZERO) == 0) {
            if (todayVal.compareTo(BigDecimal.ZERO) == 0) {
                return 0.0;
            }
            return 100.0;
        }

        return todayVal.subtract(yesterdayVal)
                .divide(yesterdayVal, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
