package hotel_booking.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {
    @NotNull(message = "Availability request details cannot be null")
    @Valid
    private CheckAvailabilityRequest availabilityRequest;

    private Integer customerId;

    @NotBlank(message = "Customer name cannot be blank")
    private String customerName;

    @NotBlank(message = "Customer phone cannot be blank")
    private String customerPhone;

    @NotBlank(message = "Customer email cannot be blank")
    @Email(message = "Invalid email format")
    private String customerEmail;

    private String bookingSource;
    private String notes;
}