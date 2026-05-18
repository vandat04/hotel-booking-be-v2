package hotel_booking.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FilterBookingRequest {
    // ===== BOOKING STATUS =====
    private String status;
    // ===== PAYMENT STATUS =====
    private String paymentStatus;
    // ===== ROOM TYPE =====
    private Integer roomTypeId;
    // ===== DATE RANGE =====
    private LocalDateTime fromDate;
    private LocalDateTime toDate;

    private String bookingSource;
}
