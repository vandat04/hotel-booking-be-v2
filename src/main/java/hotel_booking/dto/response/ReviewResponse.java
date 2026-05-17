package hotel_booking.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    // ===== REVIEW INFO =====
    private Integer id;
    private Integer rating;
    private String comment;
    private String hotelReply;
    private LocalDateTime repliedAt;
    private LocalDateTime createdAt;

    // ===== CUSTOMER INFO =====
    private CustomerInfo customer;

    // ===== ROOM TYPE INFO =====
    private RoomTypeInfo roomType;

    // ===== INNER DTO: CUSTOMER =====
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CustomerInfo {
        private Integer id;
        private String fullName;
        private String email;
    }

    // ===== INNER DTO: ROOM TYPE =====
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoomTypeInfo {
        private Integer id;
        private String name;
        private Double roomSizeM2;
        private Integer maxAdults;
        private Integer maxChildren;
        private BigDecimal pricePerDay;
        private BigDecimal pricePerHour;
    }
}