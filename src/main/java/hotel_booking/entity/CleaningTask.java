package hotel_booking.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "CleaningTasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CleaningTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ===== ROOM =====
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    // ===== CLEANER =====
    @ManyToOne
    @JoinColumn(name = "cleaner_id")
    private User cleaner;

    // ===== BOOKING =====
    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    /*
        PENDING
        DOING
        DONE
     */
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // ===== AUTO TIME =====
    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();

        if (status == null) {
            status = "PENDING";
        }
    }
}
