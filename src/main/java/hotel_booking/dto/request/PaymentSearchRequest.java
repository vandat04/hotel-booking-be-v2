package hotel_booking.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PaymentSearchRequest extends PaginationRequest {

    // ===== SEARCH =====
    private String keyword;

    // ===== FILTER =====
    private String status;

    private String paymentMethod;

    // WEB | WALK-IN | AGODA | BOOKING | EXPEDIA
    private String bookingSource;

    // OTA FILTER
    private String otaChannel;

    // ===== DATE RANGE =====
    private LocalDate fromDate;

    private LocalDate toDate;

    // ===== AMOUNT RANGE =====
    private BigDecimal minAmount;

    private BigDecimal maxAmount;
}