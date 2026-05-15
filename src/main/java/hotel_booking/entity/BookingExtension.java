package hotel_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "BookingExtensions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingExtension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(name = "old_end_at")
    private LocalDateTime oldEndAt;

    @Column(name = "new_end_at")
    private LocalDateTime newEndAt;

    @Column(name = "extension_fee")
    private BigDecimal extensionFee;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String reason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}