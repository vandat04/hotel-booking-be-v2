
package hotel_booking.dto.request;

import lombok.Data;

@Data
public class BookingDashboardRequest {
    private Integer year;
    private Integer month; // null = cả năm
}