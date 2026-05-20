package hotel_booking.service;

import hotel_booking.dto.response.WeeklyOccupancyDTO;
import hotel_booking.repository.RoomScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OccupancyAnalyticsService {
    private final RoomScheduleRepository roomScheduleRepository;

    public List<WeeklyOccupancyDTO> getWeeklyActiveOccupancy() {

        // 1. Xác định tuần hiện tại (Monday → Sunday)
        LocalDate today = LocalDate.now();

        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDate sunday = today.with(DayOfWeek.SUNDAY);

        LocalDateTime startOfWeek = monday.atStartOfDay();
        LocalDateTime endOfWeek = sunday.atTime(23, 59, 59);

        // 2. Query DB
        List<Object[]> results =
                roomScheduleRepository.getWeeklyActiveOccupancy(startOfWeek, endOfWeek);

        // 3. Convert result
        Map<String, Long> map = results.stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> ((Number) r[1]).longValue()
                ));

        // 4. Build full week (đảm bảo đủ T2 → CN kể cả ngày 0)
        List<WeeklyOccupancyDTO> response = new ArrayList<>();

        for (DayOfWeek day : DayOfWeek.values()) {
            if (day == DayOfWeek.MONDAY ||
                    day == DayOfWeek.TUESDAY ||
                    day == DayOfWeek.WEDNESDAY ||
                    day == DayOfWeek.THURSDAY ||
                    day == DayOfWeek.FRIDAY ||
                    day == DayOfWeek.SATURDAY ||
                    day == DayOfWeek.SUNDAY) {

                String key = day.toString(); // MONDAY, TUESDAY...

                response.add(new WeeklyOccupancyDTO(
                        key,
                        map.getOrDefault(key, 0L)
                ));
            }
        }

        return response;
    }
}
