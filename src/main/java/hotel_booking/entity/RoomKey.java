package hotel_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "RoomKeys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "room_schedule_id")
    private RoomSchedule roomSchedule;

    @Column( name = "code_number")
    private String codeNumber;

    @Column( name = "qr_code_data" )
    private String qrCodeData;

    @Column(name = "activated_at")
    private LocalDateTime activatedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}