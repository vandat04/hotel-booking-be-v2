package hotel_booking.exception;

import org.springframework.http.HttpStatus;

public class DuplicateDataException extends AppException {
    public DuplicateDataException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
