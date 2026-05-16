package hotel_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ===== BOOKING =====
    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    // ===== PAYMENT =====
    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    // ===== INVOICE INFO =====
    @Column(name = "invoice_number", insertable = false, updatable = false)
    private String invoiceNumber;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "amount_paid")
    private BigDecimal amountPaid;

    @Column(name = "invoice_description")
    private String invoiceDescription;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "is_sent_email")
    private Boolean isSentEmail;
}
