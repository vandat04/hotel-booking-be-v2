package hotel_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "RoomDamages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDamage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ===== BOOKING =====
    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    // ===== ITEM =====
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private BaseItem item;

    // ===== DAMAGE INFO =====
    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "actual_damage_fee")
    private BigDecimal actualDamageFee;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String note;

    @Column(name = "evidence_image_url")
    private String evidenceImageUrl;

    @Column(name = "reported_at")
    private LocalDateTime reportedAt;
}
