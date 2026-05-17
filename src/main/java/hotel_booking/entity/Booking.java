package hotel_booking.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ===== CUSTOMER =====
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "customer_email")
    private String customerEmail;

    // ===== ROOM TYPE =====
    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    @Column(name = "requested_quantity")
    private Integer requestedQuantity;

    @Column(name = "requested_checkin")
    private LocalDateTime requestedCheckin;

    @Column(name = "requested_checkout")
    private LocalDateTime requestedCheckout;

    @Column(name = "booking_type")
    private String bookingType;

    @Column(name = "booking_source")
    private String bookingSource;

    private String status;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===== RELATION =====
    @OneToMany(mappedBy = "booking")
    private List<BookingExtension> bookingExtensions;

    @OneToMany(mappedBy = "booking")
    private List<RoomSchedule> roomSchedules;

    @OneToMany(mappedBy = "booking")
    private List<Payment> payments;

    @OneToMany(mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Invoice> invoices;

    @OneToMany(mappedBy = "booking")
    private List<CleaningTask> cleaningTasks;
}
