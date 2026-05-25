package hotel_booking.service;

import hotel_booking.dto.response.RoomTimelineResponse;
import hotel_booking.entity.Room;
import hotel_booking.entity.RoomSchedule;
import hotel_booking.repository.RoomRepository;
import hotel_booking.repository.RoomScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomTimelineService {

    private final RoomRepository roomRepository;
    private final RoomScheduleRepository roomScheduleRepository;

    public List<RoomTimelineResponse> getRoomTimeline(LocalDate date, Integer floor, Integer roomTypeId) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        LocalDateTime now = LocalDateTime.now();

        // 1. Fetch active rooms
        List<Room> allRooms = roomRepository.findAllActiveRooms();

        // Filter rooms based on optional params
        List<Room> filteredRooms = allRooms.stream()
                .filter(r -> floor == null || r.getFloor().equals(floor))
                .filter(r -> roomTypeId == null || r.getRoomType().getId().equals(roomTypeId))
                .collect(Collectors.toList());

        // 2. Fetch overlapping schedules for the date
        List<RoomSchedule> allSchedules = roomScheduleRepository.findSchedulesOverlappingDay(startOfDay, endOfDay);

        // Group schedules by room
        List<RoomTimelineResponse> timelineResponses = new ArrayList<>();
        for (Room room : filteredRooms) {
            List<RoomSchedule> roomSchedules = allSchedules.stream()
                    .filter(s -> s.getRoom().getId().equals(room.getId()))
                    .collect(Collectors.toList());

            List<RoomTimelineResponse.ScheduleBlock> blocks = roomSchedules.stream()
                    .map(s -> {
                        boolean isLongStay = s.getStartAt().isBefore(startOfDay) || s.getEndAt().isAfter(endOfDay);
                        boolean isOverdue = "ACTIVE".equalsIgnoreCase(s.getStatus()) && s.getEndAt().isBefore(now);

                        return RoomTimelineResponse.ScheduleBlock.builder()
                                .scheduleId(s.getId())
                                .bookingId(s.getBooking().getId())
                                .customerName(s.getBooking().getCustomerName())
                                .customerPhone(s.getBooking().getCustomerPhone())
                                .bookingType(s.getBooking().getBookingType())
                                .startAt(s.getStartAt())
                                .endAt(s.getEndAt())
                                .status(s.getStatus())
                                .isLongStay(isLongStay)
                                .isOverdue(isOverdue)
                                .build();
                    })
                    .collect(Collectors.toList());

            timelineResponses.add(RoomTimelineResponse.builder()
                    .roomId(room.getId())
                    .roomNumber(room.getRoomNumber())
                    .floor(room.getFloor())
                    .roomTypeName(room.getRoomType().getName())
                    .status(room.getStatus())
                    .schedules(blocks)
                    .build());
        }

        return timelineResponses;
    }
}
