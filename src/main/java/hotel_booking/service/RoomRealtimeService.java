package hotel_booking.service;

import hotel_booking.dto.response.RoomTodayStatusResponse;
import hotel_booking.dto.response.RoomTypeTodayResponse;
import hotel_booking.entity.Room;
import hotel_booking.entity.RoomSchedule;
import hotel_booking.repository.RoomRepository;
import hotel_booking.repository.RoomScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomRealtimeService {
    private final RoomRepository roomRepository;
    private final RoomScheduleRepository scheduleRepository;

    public List<RoomTypeTodayResponse> getTodayRoomStatus() {

        // 1. Lấy tất cả room active
        List<Room> rooms = roomRepository.findAllActiveRooms();

        // 2. Lấy schedule đang hoạt động hôm nay
        List<RoomSchedule> schedules = scheduleRepository.findTodayActiveSchedules();

        // 3. Map schedule theo roomId
        Map<Integer, RoomSchedule> scheduleMap = schedules.stream()
                .collect(Collectors.toMap(
                        rs -> rs.getRoom().getId(),
                        rs -> rs,
                        (a, b) -> a // tránh duplicate
                ));

        // 4. Build response từng room
        List<RoomTodayStatusResponse> roomResponses = rooms.stream()
                .map(room -> {

                    RoomSchedule schedule = scheduleMap.get(room.getId());

                    String status;
                    String customerName = null;

                    if (schedule != null) {
                        status = schedule.getStatus();

                        if ("ACTIVE".equals(schedule.getStatus())) {
                            customerName = schedule.getBooking().getCustomerName();
                        }

                    } else {
                        status = room.getStatus(); // READY / DIRTY / MAINTENANCE
                    }

                    return RoomTodayStatusResponse.builder()
                            .roomId(room.getId())
                            .roomNumber(room.getRoomNumber())
                            .floor(room.getFloor())
                            .roomTypeName(room.getRoomType().getName())
                            .roomStatus(status)
                            .customerName(customerName)
                            .build();
                })
                .toList();

        // 5. Group theo RoomType
        Map<Integer, RoomTypeTodayResponse> grouped = new LinkedHashMap<>();

        for (RoomTodayStatusResponse r : roomResponses) {

            Integer roomTypeId = rooms.stream()
                    .filter(x -> x.getId().equals(r.getRoomId()))
                    .findFirst()
                    .get()
                    .getRoomType()
                    .getId();

            grouped.computeIfAbsent(roomTypeId, id -> RoomTypeTodayResponse.builder()
                    .roomTypeId(id)
                    .roomTypeName(r.getRoomTypeName())
                    .rooms(new ArrayList<>())
                    .build()
            ).getRooms().add(r);
        }

        return new ArrayList<>(grouped.values());
    }
}
