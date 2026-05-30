package hotel_booking.service;

import hotel_booking.dto.request.CreateRoomRequest;
import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.request.UpdateRoomRequest;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.dto.response.RoomResponse;
import hotel_booking.entity.Room;
import hotel_booking.entity.RoomType;
import hotel_booking.repository.CleaningTaskRepository;
import hotel_booking.repository.RoomRepository;
import hotel_booking.repository.RoomScheduleRepository;
import hotel_booking.repository.RoomTypeRepository;
import hotel_booking.util.RoomPaginationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final CleaningTaskRepository cleaningTaskRepository;
    private final RoomScheduleRepository roomScheduleRepository;
    private final hotel_booking.mapper.RoomMapper roomMapper;

    // ==================================
    // ========= CREATE ROOM  =========
    // ==================================
    public RoomResponse createRoom(CreateRoomRequest request) {
        // ===== CHECK ROOM TYPE =====
        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new RuntimeException("ROOM_TYPE_NOT_FOUND"));
        // ===== CHECK DUPLICATE ROOM NUMBER =====
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new RuntimeException("ROOM_NUMBER_ALREADY_EXISTS");
        }
        // ===== VALIDATE ALLOCATED FOR =====
        String allocatedFor = request.getAllocatedFor();

        if (allocatedFor == null || (!allocatedFor.equals("DAILY") && !allocatedFor.equals("HOURLY"))) {
            allocatedFor = "DAILY";
        }

        // ===== CREATE ROOM =====
        Room room = roomMapper.toEntity(request);
        room.setRoomType(roomType);
        room.setAllocatedFor(allocatedFor);

        roomRepository.save(room);

        return roomMapper.toResponse(room);
    }

    // ==================================
    // ========= UPDATE ROOM INFO =========
    // ==================================
    public RoomResponse updateRoom(
            Integer roomId,
            UpdateRoomRequest request
    ) {
        // ===== FIND ROOM =====
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("ROOM_NOT_FOUND"));

        // ===== CHECK ROOM TYPE =====
        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new RuntimeException("ROOM_TYPE_NOT_FOUND"));

        // ===== CHECK DUPLICATE ROOM NUMBER =====
        if (roomRepository.existsByRoomNumberAndIdNot(request.getRoomNumber(), roomId)) {
            throw new RuntimeException("ROOM_NUMBER_ALREADY_EXISTS");
        }

        // ===== VALIDATE ALLOCATED FOR =====
        String allocatedFor = request.getAllocatedFor();

        if (!allocatedFor.equals("DAILY") && !allocatedFor.equals("HOURLY")) {
            throw new RuntimeException("INVALID_ALLOCATED_FOR");
        }

        // ===== VALIDATE STATUS =====
        String status = request.getStatus();

        if (!status.equals("READY") && !status.equals("DIRTY") && !status.equals("MAINTENANCE")) {
            throw new RuntimeException("INVALID_ROOM_STATUS");
        }

        // ===== UPDATE ROOM =====
        room.setRoomType(roomType);
        room.setRoomNumber(request.getRoomNumber());
        room.setFloor(request.getFloor());
        room.setAllocatedFor(allocatedFor);
        room.setStatus(status);
        room.setIsActive(request.getIsActive());
        roomRepository.save(room);
        return roomMapper.toResponse(room);
    }

    // ==================================
    // ========= DELETE ROOM =========
    // ==================================
    @Transactional
    public void deleteRoom(Integer roomId) {

        // ===== FIND ROOM =====
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("ROOM_NOT_FOUND"));

        // ===== CHECK USED =====
       // boolean hasCleaningTask = cleaningTaskRepository.existsByRoomId(roomId);
        boolean hasRoomSchedule = roomScheduleRepository.existsByRoomId(roomId);

        // ===== SOFT DELETE =====
        //if (hasCleaningTask || hasRoomSchedule) {
        if (hasRoomSchedule) {
            room.setIsActive(false);
            roomRepository.save(room);
            return;
        }

        // ===== HARD DELETE =====
        roomRepository.delete(room);
    }

    // ==================================
    // ========= VIEW ROOM LIST =========
    // ==================================
    public PageResponse<RoomResponse> getAllRooms(
            Integer roomTypeId,
            String filterActive,
            PaginationRequest request
    ) {

        Pageable pageable = RoomPaginationUtil.build(request);
        Page<Room> roomPage;

        // Determine active filtering strategy
        // Supported values: "all", "active", "inactive"
        // Default to "active" if null or empty to preserve existing behavior
        String activeStrategy = (filterActive == null || filterActive.trim().isEmpty()) ? "active" : filterActive.trim().toLowerCase();

        if ("inactive".equals(activeStrategy)) {
            if (roomTypeId != null) {
                roomPage = roomRepository.findByRoomTypeIdAndIsActiveFalse(roomTypeId, pageable);
            } else {
                roomPage = roomRepository.findAllByIsActiveFalse(pageable);
            }
        } else if ("all".equals(activeStrategy)) {
            if (roomTypeId != null) {
                roomPage = roomRepository.findByRoomTypeId(roomTypeId, pageable);
            } else {
                roomPage = roomRepository.findAll(pageable);
            }
        } else {
            // Default: active only
            if (roomTypeId != null) {
                roomPage = roomRepository.findByRoomTypeIdAndIsActiveTrue(roomTypeId, pageable);
            } else {
                roomPage = roomRepository.findAllByIsActiveTrue(pageable);
            }
        }

        // ===== MAP RESPONSE =====
        List<RoomResponse> content = roomPage.getContent()
                .stream()
                .map(roomMapper::toResponse)
                .toList();

        return PageResponse.<RoomResponse>builder()
                .content(content)
                .page(roomPage.getNumber())
                .size(roomPage.getSize())
                .totalElements(roomPage.getTotalElements())
                .totalPages(roomPage.getTotalPages())
                .last(roomPage.isLast())
                .build();
    }

    // ==================================
    // ========= VIEW ROOM DETAIL =========
    // ==================================
    public RoomResponse getRoomDetail(Integer roomId) {
        // ===== FIND ROOM =====
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("ROOM_NOT_FOUND"));

        // ===== MAP RESPONSE =====
        return roomMapper.toResponse(room);
    }
}
