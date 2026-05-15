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

    // ================= SEARCH ROOM TYPES =================
    public PageResponse<GuestSearchRoomResponse> search(GuestSearchRoomRequest request) {

        // ===== VALIDATE =====
        if (request.getBookingType() == null || (!request.getBookingType().equals("DAILY") && !request.getBookingType().equals("HOURLY"))) {

            throw new RuntimeException("Booking type must be DAILY or HOURLY");
        }

        Pageable pageable = PaginationUtil.build(request);

        BigDecimal minPrice = request.getMinPrice() != null ? request.getMinPrice() : BigDecimal.ZERO;

        BigDecimal maxPrice = request.getMaxPrice() != null ? request.getMaxPrice() : new BigDecimal("999999999");

        // ===== QUERY =====
        Page<RoomType> roomTypePage;

        if (request.getBookingType().equals("DAILY")) {

            roomTypePage = roomTypeRepository.findByStatusAndPricePerDayBetween(1, minPrice, maxPrice, pageable);

        } else {

            roomTypePage = roomTypeRepository.findByStatusAndPricePerHourBetween(1, minPrice, maxPrice, pageable);
        }

        // ===== MAP RESPONSE =====
        List<GuestSearchRoomResponse> content = roomTypePage.getContent().stream().map(roomType -> {

            // ===== FILTER ADULT =====
            if (request.getAdults() != null && roomType.getMaxAdults() < request.getAdults()) {

                return null;
            }

            // ===== FILTER CHILDREN =====
            if (request.getChildren() != null && roomType.getMaxChildren() < request.getChildren()) {

                return null;
            }

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

            return GuestSearchRoomResponse.builder().roomTypeId(roomType.getId()).roomTypeName(roomType.getName()).description(roomType.getDescription()).pricePerDay(roomType.getPricePerDay()).pricePerHour(roomType.getPricePerHour()).maxAdults(roomType.getMaxAdults()).maxChildren(roomType.getMaxChildren()).bedCount(roomType.getBedCount()).bedType(roomType.getBedType()).roomSizeM2(roomType.getRoomSizeM2()).thumbnail(thumbnail).build();
        }).filter(java.util.Objects::nonNull).toList();

        // ===== RESPONSE =====
        return PageResponse.<GuestSearchRoomResponse>builder().content(content).page(roomTypePage.getNumber()).size(roomTypePage.getSize()).totalElements(roomTypePage.getTotalElements()).totalPages(roomTypePage.getTotalPages()).last(roomTypePage.isLast()).build();
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
        return RoomTypeDetailResponse.builder().roomTypeId(roomType.getId()).roomTypeName(roomType.getName()).description(roomType.getDescription()).pricePerDay(roomType.getPricePerDay()).pricePerHour(roomType.getPricePerHour()).maxAdults(roomType.getMaxAdults()).maxChildren(roomType.getMaxChildren()).bedCount(roomType.getBedCount()).bedType(roomType.getBedType()).roomSizeM2(roomType.getRoomSizeM2()).images(images).items(items)
                // ===== REVIEW =====
                .averageRating(avgRating).totalReviews(totalReviews).reviews(reviews).build();
    }
}
