package hotel_booking.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Object errors;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .errors(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success("Successfully", data);
    }

    public static <T> ApiResponse<T> error(String message, Object errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return error(message, null);
    }
}
