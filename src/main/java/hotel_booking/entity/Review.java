package hotel_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ===== BOOKING =====
    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    // ===== CUSTOMER =====
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @Column(name = "customer_name")
    private String customerName;

    // ===== ROOM TYPE =====
    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    private Integer rating;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String comment;

    @Column(name = "hotel_reply", columnDefinition = "NVARCHAR(MAX)")
    private String hotelReply;

    @Column(name = "replied_at")
    private LocalDateTime repliedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
