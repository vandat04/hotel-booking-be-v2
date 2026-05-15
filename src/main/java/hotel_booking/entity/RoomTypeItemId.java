package hotel_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeItemId implements Serializable {

    @Column(name = "room_type_id")
    private Integer roomTypeId;

    @Column(name = "item_id")
    private Integer itemId;
}
