package hotel_booking.util;

import hotel_booking.dto.request.PaginationRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class AttendancePaginationUtil {

    private static final int MAX_SIZE = 50;

    private static final List<String> ALLOWED_SORT_FIELDS =
            List.of(
                    "id",
                    "createdAt",
                    "workDate",
                    "status",
                    "checkIn",
                    "checkOut"
            );

    public static Pageable build(PaginationRequest req) {

        int page = Math.max(req.getPage(), 0);

        int size = req.getSize() <= 0
                ? 10
                : Math.min(req.getSize(), MAX_SIZE);

        String sortBy = req.getSortBy();
        String direction = req.getDirection();

        // ===== DEFAULT SORT =====
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "createdAt";
        }

        // ===== VALIDATE SORT FIELD =====
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            sortBy = "createdAt";
        }

        // ===== DEFAULT DIRECTION =====
        if (direction == null || direction.isBlank()) {
            direction = "desc";
        }

        Sort sort = "asc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        return PageRequest.of(page, size, sort);
    }
}
