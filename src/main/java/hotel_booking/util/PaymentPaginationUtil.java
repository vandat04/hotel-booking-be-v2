package hotel_booking.util;

import hotel_booking.dto.request.PaginationRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class PaymentPaginationUtil {

    private static final int MAX_SIZE = 50;

    private static final List<String> ALLOWED_SORT_FIELDS =
            List.of(
                    "id",
                    "amount",
                    "paymentDate",
                    "status",
                    "paymentMethod"
            );

    public static Pageable build(PaginationRequest req) {

        int page = Math.max(req.getPage(), 0);

        int size = Math.min(req.getSize(), MAX_SIZE);

        String sortBy = req.getSortBy();

        String direction = req.getDirection();

        // ===== VALIDATE SORT FIELD =====
        if (sortBy == null
                || sortBy.isBlank()
                || !ALLOWED_SORT_FIELDS.contains(sortBy)) {

            sortBy = "paymentDate";
        }

        // ===== VALIDATE DIRECTION =====
        if (direction == null || direction.isBlank()) {
            direction = "desc";
        }

        Sort sort = "desc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        return PageRequest.of(page, size, sort);
    }
}