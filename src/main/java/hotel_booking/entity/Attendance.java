package hotel_booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Attendance")
@Getter
@Setter
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // USER
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // ASSIGN STAFF
    @ManyToOne
    @JoinColumn(name = "shift_assignment_id")
    private AssignStaff shiftAssignment;

    @Column(name = "check_in")
    private LocalDateTime checkIn;

    @Column(name = "check_out")
    private LocalDateTime checkOut;

    @Column(name = "work_hours")
    private BigDecimal workHours = BigDecimal.ZERO;

    @Column(name = "late_minutes")
    private Integer lateMinutes = 0;

    @Column(name = "early_leave_minutes")
    private Integer earlyLeaveMinutes = 0;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
