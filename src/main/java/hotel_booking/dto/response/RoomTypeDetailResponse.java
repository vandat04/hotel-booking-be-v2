package hotel_booking.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomTypeDetailResponse {

    // ===== ROOM TYPE =====
    private Integer roomTypeId;
    private String roomTypeName;
    private String description;

    private BigDecimal pricePerDay;
    private BigDecimal pricePerHour;

    private Integer maxAdults;
    private Integer maxChildren;
    private Integer bedCount;
    private String bedType;
    private Double roomSizeM2;

    // ===== IMAGES =====
    private List<RoomImageDTO> images;

    // ===== ITEMS =====
    private List<RoomItemDTO> items;

    // ===== REVIEW =====
    private Double averageRating;
    private Integer totalReviews;
    private List<ReviewDTO> reviews;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ReviewDTO {
        private String customerName;
        private Integer rating;
        private String comment;
        private String hotelReply;
        private java.time.LocalDateTime createdAt;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RoomItemDTO {
        private String itemName;
        private String description;
        private Integer quantity;
        private BigDecimal baseUnitPrice;
        private String itemImageUrl;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RoomImageDTO {
        private String imageUrl;
        private Boolean isPrimary;
        private String caption;
    }
}
