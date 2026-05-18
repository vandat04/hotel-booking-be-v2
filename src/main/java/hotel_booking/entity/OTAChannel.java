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

    @Column(name = "ota_hotel_id", length = 100)
    private String otaHotelId;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "api_key_secret", length = 255)
    private String apiKeySecret;

    @Column(name = "webhook_secret", length = 255)
    private String webhookSecret;

    @Column(name = "is_active")
    private Boolean isActive = true;
}