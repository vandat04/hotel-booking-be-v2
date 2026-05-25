package hotel_booking.service;

import hotel_booking.dto.response.WeeklyOccupancyDTO;
import hotel_booking.entity.RoomSchedule;
import hotel_booking.repository.RoomScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OccupancyAnalyticsService {
    private final RoomScheduleRepository roomScheduleRepository;

    public List<WeeklyOccupancyDTO> getWeeklyActiveOccupancy() {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDate sunday = today.with(DayOfWeek.SUNDAY);

        LocalDateTime startOfWeek = monday.atStartOfDay();
        System.out.println(startOfWeek);
        LocalDateTime endOfWeek = sunday.atTime(23, 59, 59);
        System.out.println(endOfWeek);

        // Fetch all schedules that overlap with the current week
        List<RoomSchedule> schedules = roomScheduleRepository.findOverlappingSchedules(startOfWeek, endOfWeek);

        // Initialize daily counts to 0
        Map<DayOfWeek, Long> dailyCounts = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            dailyCounts.put(day, 0L);
        }

        // Iterate through all overlapping schedules
        for (RoomSchedule rs : schedules) {
            LocalDateTime rsStart = rs.getStartAt();
            LocalDateTime rsEnd = rs.getEndAt();

            // For each schedule, check which days of the week it overlaps with
            for (int i = 0; i < 7; i++) {
                LocalDate targetDate = monday.plusDays(i);
                LocalDateTime startOfDay = targetDate.atStartOfDay();
                LocalDateTime endOfDay = targetDate.atTime(23, 59, 59);

                // A room is considered occupied on the target date if its schedule falls within the day
                if (rsStart.isBefore(endOfDay) && rsEnd.isAfter(startOfDay)) {
                    DayOfWeek day = targetDate.getDayOfWeek();
                    dailyCounts.put(day, dailyCounts.get(day) + 1);
                }
            }
        }

        // Build the final response list
        List<WeeklyOccupancyDTO> response = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            response.add(new WeeklyOccupancyDTO(day.toString(), dailyCounts.get(day)));
        }

        return response;
    }
}
