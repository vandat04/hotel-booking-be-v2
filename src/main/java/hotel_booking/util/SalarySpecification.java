package hotel_booking.util;

import hotel_booking.dto.request.SalaryFilterRequest;
import hotel_booking.entity.SalarySheet;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SalarySpecification {

    public static Specification<SalarySheet> filterSalary(
            SalaryFilterRequest request
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // ===== FILTER MONTH =====
            if (request.getMonth() != null) {

                predicates.add(
                        cb.equal(
                                root.get("month"),
                                request.getMonth()
                        )
                );
            }

            // ===== FILTER YEAR =====
            if (request.getYear() != null) {

                predicates.add(
                        cb.equal(
                                root.get("year"),
                                request.getYear()
                        )
                );
            }

            // ===== FILTER USER =====
            if (request.getUserId() != null) {

                predicates.add(
                        cb.equal(
                                root.get("user").get("id"),
                                request.getUserId()
                        )
                );
            }

            // ===== FILTER ROLE =====
            if (request.getRole() != null
                    && !request.getRole().isBlank()) {

                predicates.add(
                        cb.equal(
                                root.get("user").get("role"),
                                request.getRole()
                        )
                );
            }

            // ===== FILTER STATUS =====
            if (request.getStatus() != null
                    && !request.getStatus().isBlank()) {

                String queryStatus = request.getStatus();
                if ("UNPAID".equalsIgnoreCase(queryStatus)) {
                    queryStatus = "PENDING";
                }

                predicates.add(
                        cb.equal(
                                root.get("status"),
                                queryStatus
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}