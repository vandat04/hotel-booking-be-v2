package hotel_booking.repository;


import hotel_booking.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Integer> {

    Optional<Shift> findByShiftNameIgnoreCase(String shiftName);


    boolean existsByShiftNameIgnoreCase(String shiftName);

    @Query(value = """
            SELECT 
                CASE 
                    WHEN COUNT(*) > 0 THEN 1 
                    ELSE 0 
                END
            FROM Shifts
            WHERE start_time = CAST(:startTime AS TIME)
              AND end_time = CAST(:endTime AS TIME)
            """, nativeQuery = true)
    int existsShiftTime(
            @Param("startTime") String startTime,
            @Param("endTime") String endTime
    );


    Optional<Shift> findByIdAndIsActiveTrue(Integer id);

    boolean existsByShiftNameIgnoreCaseAndIdNot(
            String shiftName,
            Integer id
    );

    // lấy tất cả
    List<Shift> findAllByOrderByIdDesc();

    // lọc theo isActive
    List<Shift> findByIsActiveOrderByIdDesc(Boolean isActive);
}
