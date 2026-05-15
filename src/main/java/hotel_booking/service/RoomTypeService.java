package hotel_booking.service;

import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.response.RoomTypeListResponse;
import hotel_booking.entity.RoomType;
import hotel_booking.entity.RoomTypeImage;
import hotel_booking.repository.RoomTypeImageRepository;
import hotel_booking.repository.RoomTypeRepository;
import hotel_booking.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomTypeService {
    private final RoomTypeRepository roomTypeRepository;
    private final RoomTypeImageRepository imageRepository;

    public Page<RoomTypeListResponse> getActiveRoomTypes(PaginationRequest request) {

        Pageable pageable = PaginationUtil.build(request);

        Page<RoomType> roomTypes =
                roomTypeRepository.findByStatus(1, pageable);

        return roomTypes.map(roomType -> {

            // ===== THUMBNAIL =====
            String thumbnail = null;

            RoomTypeImage primary =
                    imageRepository
                            .findFirstByRoomTypeIdAndIsPrimaryTrue(roomType.getId())
                            .orElse(null);

            if (primary != null) {
                thumbnail = primary.getImageUrl();
            } else {
                RoomTypeImage first =
                        imageRepository
                                .findFirstByRoomTypeIdOrderByIdAsc(roomType.getId())
                                .orElse(null);

                if (first != null) {
                    thumbnail = first.getImageUrl();
                }
            }

            // ===== MAP RESPONSE =====
            return RoomTypeListResponse.builder()
                    .id(roomType.getId())
                    .name(roomType.getName())
                    .description(roomType.getDescription())
                    .pricePerDay(roomType.getPricePerDay())
                    .pricePerHour(roomType.getPricePerHour())
                    .maxAdults(roomType.getMaxAdults())
                    .maxChildren(roomType.getMaxChildren())
                    .bedCount(roomType.getBedCount())
                    .bedType(roomType.getBedType())
                    .roomSizeM2(roomType.getRoomSizeM2())
                    .thumbnail(thumbnail)
                    .build();
        });
    }
}
