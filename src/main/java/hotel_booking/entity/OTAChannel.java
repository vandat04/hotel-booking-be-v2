package hotel_booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "OTAChannels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OTAChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ota_hotel_id")
    private String otaHotelId;

    private String name;

    @Column(name = "api_key_secret")
    private String apiKeySecret;

    @Column(name = "webhook_secret")
    private String webhookSecret;

    @Column(name = "is_active")
    private Boolean isActive;
}
