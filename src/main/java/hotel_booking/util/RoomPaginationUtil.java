package hotel_booking.util;

import hotel_booking.dto.request.PaginationRequest;
import org.springframework.data.domain.*;

import java.util.List;

public class RoomPaginationUtil {

    private static final int MAX_SIZE = 50;

    private static final List<String> ALLOWED_SORT_FIELDS =
            List.of(
                    "id",
                    "roomNumber",
                    "floor",
                    "status",
                    "allocatedFor",
                    "createdAt"
            );

    public static Pageable build(PaginationRequest req) {

        int page = Math.max(req.getPage(), 0);

        int size = Math.min(req.getSize(), MAX_SIZE);

        String sortBy = req.getSortBy();

        String direction = req.getDirection();

        // validate sort field
        if (sortBy == null
                || sortBy.isBlank()
                || !ALLOWED_SORT_FIELDS.contains(sortBy)) {

            sortBy = "id";
        }

        // validate direction
        if (direction == null) {
            direction = "asc";
        }

        Sort sort = "desc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        return PageRequest.of(page, size, sort);
    }
}
