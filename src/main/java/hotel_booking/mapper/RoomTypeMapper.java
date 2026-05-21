package hotel_booking.mapper;

import hotel_booking.dto.request.CreateRoomTypeRequest;
import hotel_booking.dto.request.UpdateRoomTypeRequest;
import hotel_booking.dto.response.GuestSearchRoomResponse;
import hotel_booking.dto.response.RoomTypeDetailResponse;
import hotel_booking.dto.response.RoomTypeListResponse;
import hotel_booking.dto.response.RoomTypeResponse;
import hotel_booking.entity.RoomType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RoomTypeMapper {

    public RoomType toEntity(CreateRoomTypeRequest request) {
        if (request == null) {
            return null;
        }
        return RoomType.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status(request.getStatus())
                .pricePerDay(request.getPricePerDay())
                .pricePerHour(request.getPricePerHour())
                .targetDailyPercentage(request.getTargetDailyPercentage())
                .targetHourlyPercentage(request.getTargetHourlyPercentage())
                .maxAdults(request.getMaxAdults())
                .maxChildren(request.getMaxChildren())
                .bedCount(request.getBedCount())
                .bedType(request.getBedType())
                .roomSizeM2(request.getRoomSizeM2())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void updateEntity(UpdateRoomTypeRequest request, RoomType roomType) {
        if (request == null || roomType == null) {
            return;
        }
        roomType.setName(request.getName());
        roomType.setDescription(request.getDescription());
        roomType.setStatus(request.getStatus());
        roomType.setPricePerDay(request.getPricePerDay());
        roomType.setPricePerHour(request.getPricePerHour());
        roomType.setTargetDailyPercentage(request.getTargetDailyPercentage());
        roomType.setTargetHourlyPercentage(request.getTargetHourlyPercentage());
        roomType.setMaxAdults(request.getMaxAdults());
        roomType.setMaxChildren(request.getMaxChildren());
        roomType.setBedCount(request.getBedCount());
        roomType.setBedType(request.getBedType());
        roomType.setRoomSizeM2(request.getRoomSizeM2());
        roomType.setUpdatedAt(LocalDateTime.now());
    }

    public RoomTypeResponse toResponse(RoomType roomType) {
        if (roomType == null) {
            return null;
        }
        return RoomTypeResponse.builder()
                .id(roomType.getId())
                .hotelId(roomType.getHotel() != null ? roomType.getHotel().getId() : null)
                .name(roomType.getName())
                .description(roomType.getDescription())
                .status(roomType.getStatus())
                .pricePerDay(roomType.getPricePerDay())
                .pricePerHour(roomType.getPricePerHour())
                .targetDailyPercentage(roomType.getTargetDailyPercentage())
                .targetHourlyPercentage(roomType.getTargetHourlyPercentage())
                .maxAdults(roomType.getMaxAdults())
                .maxChildren(roomType.getMaxChildren())
                .bedCount(roomType.getBedCount())
                .bedType(roomType.getBedType())
                .roomSizeM2(roomType.getRoomSizeM2())
                .createdAt(roomType.getCreatedAt())
                .updatedAt(roomType.getUpdatedAt())
                .build();
    }

    public RoomTypeListResponse toTypeListResponse(RoomType roomType, String thumbnail) {
        if (roomType == null) {
            return null;
        }
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
    }

    public GuestSearchRoomResponse toSearchResponse(RoomType roomType, String thumbnail) {
        if (roomType == null) {
            return null;
        }
        return GuestSearchRoomResponse.builder()
                .roomTypeId(roomType.getId())
                .roomTypeName(roomType.getName())
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
    }

    public RoomTypeDetailResponse toDetailResponse(
            RoomType roomType,
            List<RoomTypeDetailResponse.RoomImageDTO> images,
            List<RoomTypeDetailResponse.RoomItemDTO> items,
            List<RoomTypeDetailResponse.ReviewDTO> reviews,
            double avgRating,
            int totalReviews
    ) {
        if (roomType == null) {
            return null;
        }
        return RoomTypeDetailResponse.builder()
                .roomTypeId(roomType.getId())
                .roomTypeName(roomType.getName())
                .description(roomType.getDescription())
                .pricePerDay(roomType.getPricePerDay())
                .pricePerHour(roomType.getPricePerHour())
                .maxAdults(roomType.getMaxAdults())
                .maxChildren(roomType.getMaxChildren())
                .bedCount(roomType.getBedCount())
                .bedType(roomType.getBedType())
                .roomSizeM2(roomType.getRoomSizeM2())
                .images(images)
                .items(items)
                .averageRating(avgRating)
                .totalReviews(totalReviews)
                .reviews(reviews)
                .build();
    }
}
