package hotel_booking.util;

import hotel_booking.entity.Attendance;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class AttendanceSpecification {

    public static Specification<Attendance> filterAttendance(
            LocalDate workDate,
            Integer month,
            Integer year,
            Integer userId,
            Integer shiftId
    ) {

        return (root, query, cb) -> {

            var predicates = cb.conjunction();

            // ===== FILTER WORK DATE =====
            if (workDate != null) {

                predicates = cb.and(
                        predicates,
                        cb.equal(
                                root.get("shiftAssignment")
                                        .get("workDate"),
                                workDate
                        )
                );
            }

            // ===== FILTER MONTH/YEAR =====
            if (month != null && year != null) {

                predicates = cb.and(
                        predicates,
                        cb.equal(
                                cb.function(
                                        "MONTH",
                                        Integer.class,
                                        root.get("shiftAssignment")
                                                .get("workDate")
                                ),
                                month
                        )
                );

                predicates = cb.and(
                        predicates,
                        cb.equal(
                                cb.function(
                                        "YEAR",
                                        Integer.class,
                                        root.get("shiftAssignment")
                                                .get("workDate")
                                ),
                                year
                        )
                );
            }

            // ===== FILTER USER =====
            if (userId != null) {

                predicates = cb.and(
                        predicates,
                        cb.equal(
                                root.get("user").get("id"),
                                userId
                        )
                );
            }

            // ===== FILTER SHIFT =====
            if (shiftId != null) {

                predicates = cb.and(
                        predicates,
                        cb.equal(
                                root.get("shiftAssignment")
                                        .get("shift")
                                        .get("id"),
                                shiftId
                        )
                );
            }

            return predicates;
        };
    }
}