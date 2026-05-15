package hotel_booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "HotelImages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ================= RELATIONSHIP =================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    // ================= FIELDS =================

    @Column(name = "image_url", nullable = false, columnDefinition = "VARCHAR(MAX)")
    private String imageUrl;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    @Column(name = "caption", length = 255)
    private String caption;
}
