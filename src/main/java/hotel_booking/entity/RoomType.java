package hotel_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "RoomTypes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ===== HOTEL =====
    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    // ===== BASIC INFO =====
    private String name;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    private Integer status;

    // ===== PRICE =====
    @Column(name = "price_per_day")
    private BigDecimal pricePerDay;

    @Column(name = "price_per_hour")
    private BigDecimal pricePerHour;

    // ===== TARGET =====
    @Column(name = "target_daily_percentage")
    private Integer targetDailyPercentage;

    @Column(name = "target_hourly_percentage")
    private Integer targetHourlyPercentage;

    // ===== ROOM INFO =====
    @Column(name = "max_adults")
    private Integer maxAdults;

    @Column(name = "max_children")
    private Integer maxChildren;

    @Column(name = "bed_count")
    private Integer bedCount;

    @Column(name = "bed_type")
    private String bedType;

    @Column(name = "room_size_m2")
    private Double roomSizeM2;

    // ===== AUDIT =====
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===== RELATION =====
    @OneToMany(mappedBy = "roomType")
    private List<RoomTypeImage> images;

    @OneToMany(mappedBy = "roomType")
    private List<Room> rooms;

    @OneToMany(mappedBy = "roomType")
    private List<Booking> bookings;

    @OneToMany(mappedBy = "roomType")
    private List<RoomTypeItem> roomTypeItems;
}