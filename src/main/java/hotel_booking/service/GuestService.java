package hotel_booking.service;

import hotel_booking.dto.request.GuestSearchRoomRequest;
import hotel_booking.dto.response.GuestSearchRoomResponse;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.dto.response.RoomTypeDetailResponse;
import hotel_booking.entity.BaseItem;
import hotel_booking.entity.Review;
import hotel_booking.entity.RoomType;
import hotel_booking.entity.RoomTypeImage;
import hotel_booking.repository.*;
import hotel_booking.util.PaginationUtil;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestService {
    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final RoomTypeImageRepository imageRepository;
    private final ReviewRepository reviewRepository;
    private final RoomTypeItemRepository roomTypeItemRepository;
    private final hotel_booking.mapper.RoomTypeMapper roomTypeMapper;

    // ================= SEARCH ROOM TYPES =================
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeStr);
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e2) {
                try {
                    return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                } catch (Exception e3) {
                    try {
                        return java.time.LocalDate.parse(dateTimeStr).atStartOfDay();
                    } catch (Exception e4) {
                        return null;
                    }
                }
            }
        }
    }

    public PageResponse<GuestSearchRoomResponse> search(GuestSearchRoomRequest request) {

        // ===== VALIDATE =====
        if (request.getBookingType() == null || (!request.getBookingType().equals("DAILY") && !request.getBookingType().equals("HOURLY"))) {
            throw new RuntimeException("Booking type must be DAILY or HOURLY");
        }

        Pageable pageable = PaginationUtil.build(request);

        BigDecimal minPrice = request.getMinPrice() != null ? request.getMinPrice() : BigDecimal.ZERO;
        BigDecimal maxPrice = request.getMaxPrice() != null ? request.getMaxPrice() : new BigDecimal("999999999");

        // Parse check-in, check-out dates
        LocalDateTime checkInVal = parseDateTime(request.getCheckIn());
        LocalDateTime checkOutVal = parseDateTime(request.getCheckOut());

        // ===== QUERY =====
        Page<RoomType> roomTypePage = roomTypeRepository.searchRoomTypes(
                request.getBookingType(),
                minPrice,
                maxPrice,
                request.getAdults(),
                request.getChildren(),
                checkInVal,
                checkOutVal,
                pageable
        );

        // ===== MAP RESPONSE =====
        List<GuestSearchRoomResponse> content = roomTypePage.getContent().stream().map(roomType -> {

            // ===== THUMBNAIL =====
            String thumbnail = null;

            RoomTypeImage primaryImage = imageRepository.findFirstByRoomTypeIdAndIsPrimaryTrue(roomType.getId()).orElse(null);

            if (primaryImage != null) {
                thumbnail = primaryImage.getImageUrl();
            } else {
                RoomTypeImage firstImage = imageRepository.findFirstByRoomTypeIdOrderByIdAsc(roomType.getId()).orElse(null);
                if (firstImage != null) {
                    thumbnail = firstImage.getImageUrl();
                }
            }

            return roomTypeMapper.toSearchResponse(roomType, thumbnail);
        }).toList();

        // ===== RESPONSE =====
        return PageResponse.<GuestSearchRoomResponse>builder()
                .content(content)
                .page(roomTypePage.getNumber())
                .size(roomTypePage.getSize())
                .totalElements(roomTypePage.getTotalElements())
                .totalPages(roomTypePage.getTotalPages())
                .last(roomTypePage.isLast())
                .build();
    }

    // ================= DETAIL =================
    public RoomTypeDetailResponse getDetail(Integer roomTypeId) {

        // ===== ROOM TYPE =====
        RoomType roomType = roomTypeRepository.findById(roomTypeId).orElseThrow(() -> new RuntimeException("Room type not found"));

        // ===== IMAGES =====
        List<RoomTypeDetailResponse.RoomImageDTO> images = imageRepository.findByRoomTypeId(roomTypeId).stream().map(img -> RoomTypeDetailResponse.RoomImageDTO.builder().imageUrl(img.getImageUrl()).isPrimary(img.getIsPrimary()).caption(img.getCaption()).build()).toList();

        // ===== ITEMS =====
        List<RoomTypeDetailResponse.RoomItemDTO> items = roomTypeItemRepository.findByRoomTypeId(roomTypeId).stream().map(rti -> {

            BaseItem item = rti.getItem();

            return RoomTypeDetailResponse.RoomItemDTO.builder().itemName(item.getItemName()).description(item.getDescription()).quantity(rti.getQuantity()).baseUnitPrice(item.getBaseUnitPrice()).itemImageUrl(item.getItemImageUrl()).build();
        }).toList();

        // ===== REVIEWS =====
        List<Review> reviewList = reviewRepository.findByRoomTypeId(roomTypeId);

        List<RoomTypeDetailResponse.ReviewDTO> reviews = reviewList.stream().map(r -> RoomTypeDetailResponse.ReviewDTO.builder().customerName(r.getCustomerName()).rating(r.getRating()).comment(r.getComment()).hotelReply(r.getHotelReply()).createdAt(r.getCreatedAt()).build()).toList();

        // ===== AVERAGE RATING =====
        double avgRating = 0.0;

        if (!reviewList.isEmpty()) {

            avgRating = reviewList.stream().mapToInt(Review::getRating).average().orElse(0.0);
        }

        // ===== TOTAL =====
        int totalReviews = reviewList.size();

        // ===== RESPONSE =====
        return roomTypeMapper.toDetailResponse(roomType, images, items, reviews, avgRating, totalReviews);
    }
}
