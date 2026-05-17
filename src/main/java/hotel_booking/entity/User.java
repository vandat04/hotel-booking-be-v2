package hotel_booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ================= LOGIN =================

    @Column(unique = true, length = 50)
    private String username;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    // ================= OAUTH =================

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String provider = "LOCAL";

    @Column(name = "provider_id", unique = true, length = 100)
    private String providerId;

    // ================= CONTACT =================

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;

    @Column(unique = true, length = 20)
    private String phone;

    // ================= PROFILE =================

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    /*
        1 = MALE
        2 = FEMALE
        3 = OTHER
     */
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    // ================= ROLE =================

    /*
        ADMIN
        CUSTOMER
        CLEANER
        RECEPTIONIST
     */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String role = "CUSTOMER";

    // ================= STATUS =================

    /*
        1 = ACTIVE
        2 = INACTIVE
        3 = BANNED
     */
    @Builder.Default
    private Integer status = 1;

    // ================= AUDIT =================

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ================= AUTO TIME =================

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "cleaner")
    private List<CleaningTask> cleaningTasks;}