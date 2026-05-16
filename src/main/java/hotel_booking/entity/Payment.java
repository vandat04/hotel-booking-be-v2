package hotel_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ===== BOOKING =====
    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    // ===== PAYMENT INFO =====
    private BigDecimal amount;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "gateway_name")
    private String gatewayName;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "status")
    private String status;

    @Column(name = "transaction_reference")
    private String transactionReference;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String notes;

    @OneToOne(mappedBy = "payment")
    private Invoice invoice;
}
