package hotel_booking.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateInvoiceRequest {

    private Integer bookingId;

    private Integer paymentId;

    private String invoiceDescription;
}