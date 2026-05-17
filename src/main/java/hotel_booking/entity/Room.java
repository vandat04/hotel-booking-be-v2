package hotel_booking.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    @Column(name = "room_number")
    private String roomNumber;

    private Integer floor;

    @Column(name = "allocated_for")
    private String allocatedFor;

    private String status;

    @Column(name = "expected_checkout_at")
    private LocalDateTime expectedCheckoutAt;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "room")
    private List<RoomSchedule> roomSchedules;

    @OneToMany(mappedBy = "room")
    private List<CleaningTask> cleaningTasks;
}
