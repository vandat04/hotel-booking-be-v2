package hotel_booking.repository;

import hotel_booking.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findById(Integer id);

    Optional<User> findByUsername(String username);

    Optional<User> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    @Query("""
                SELECT u
                FROM User u
                WHERE
                    (
                        :keyword IS NULL
                        OR :keyword = ''
                        OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(u.role) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    )
            
                    AND (:role IS NULL OR u.role = :role)
            
                    AND (:status IS NULL OR u.status = :status)
            
                    AND u.role IN ('CLEANER', 'RECEPTIONIST')
            """)
    Page<User> getStaffs(
            String keyword,
            String role,
            Integer status,
            Pageable pageable
    );

    Optional<User> findByIdAndRoleIn(Integer id, java.util.List<String> roles);

    // Lấy danh sách staff đang hoạt động
    List<User> findByRoleInAndStatus(
            List<String> roles,
            Integer status
    );

    long countByRoleInAndStatus(
            java.util.List<String> roles,
            Integer status
    );

    long countByRoleAndStatus(
            String role,
            Integer status
    );
}