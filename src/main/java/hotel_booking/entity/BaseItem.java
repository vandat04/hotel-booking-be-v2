package hotel_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "BaseItems")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "base_unit_price")
    private BigDecimal baseUnitPrice;

    @Column(name = "item_image_url")
    private String itemImageUrl;

    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "item")
    private List<RoomTypeItem> roomTypeItems;
}