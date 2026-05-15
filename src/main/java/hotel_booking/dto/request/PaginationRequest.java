package hotel_booking.dto.request;

import lombok.Data;

@Data
public class PaginationRequest {
    private int page = 0;
    private int size = 10;
    private String sortBy ;
    private String direction;
}
