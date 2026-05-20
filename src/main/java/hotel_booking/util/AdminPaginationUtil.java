package hotel_booking.util;

import hotel_booking.dto.request.AdminStaffListRequest;
import org.springframework.data.domain.*;

import java.util.List;

public class AdminPaginationUtil {

    private static final int MAX_SIZE = 50;

    private static final List<String> ALLOWED_SORT_FIELDS =
            List.of(
                    "id",
                    "fullName",
                    "email",
                    "role",
                    "status",
                    "createdAt"
            );

    public static Pageable build(AdminStaffListRequest req) {

        int page = Math.max(req.getPage(), 0);

        int size = req.getSize() <= 0
                ? 10
                : Math.min(req.getSize(), MAX_SIZE);

        String sortBy = req.getSortBy();

        if (sortBy == null
                || sortBy.isBlank()
                || !ALLOWED_SORT_FIELDS.contains(sortBy)) {

            sortBy = "id";
        }

        String direction = req.getDirection();

        if (direction == null) {
            direction = "desc";
        }

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        return PageRequest.of(page, size, sort);
    }
}
