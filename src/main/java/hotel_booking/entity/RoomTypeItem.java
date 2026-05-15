package hotel_booking.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "RoomTypeItems")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomTypeItem {

    @EmbeddedId
    private RoomTypeItemId id;

    @ManyToOne
    @MapsId("roomTypeId")
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    @ManyToOne
    @MapsId("itemId")
    @JoinColumn(name = "item_id")
    private BaseItem item;

    private Integer quantity;
}
