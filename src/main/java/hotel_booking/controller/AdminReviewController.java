package hotel_booking.controller;

import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.request.ReplyReviewRequest;
import hotel_booking.dto.request.SearchReviewRequest;
import hotel_booking.dto.response.ApiResponse;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.dto.response.ReviewResponse;
import hotel_booking.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    // ================= GET ALL REVIEWS (paginated, filterable) =================
    // GET /api/admin/reviews?replyStatus=REPLIED&page=0&size=10
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getAllReviews(
            PaginationRequest request,
            @RequestParam(required = false) String replyStatus
    ) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getAllReviews(request, replyStatus)));
    }

    // ================= GET REVIEW DETAIL =================
    // GET /api/admin/reviews/{reviewId}
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewDetail(@PathVariable Integer reviewId) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getReviewDetail(reviewId)));
    }

    // ================= REPLY TO REVIEW =================
    // PUT /api/admin/reviews/{reviewId}/reply
    @PutMapping("/{reviewId}/reply")
    public ResponseEntity<ApiResponse<String>> replyReview(
            @PathVariable Integer reviewId,
            @RequestBody ReplyReviewRequest request
    ) {
        reviewService.replyReview(reviewId, request);
        return ResponseEntity.ok(ApiResponse.success("Review reply submitted successfully"));
    }

    // ================= DELETE REVIEW =================
    // DELETE /api/admin/reviews/{reviewId}
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<String>> deleteReview(@PathVariable Integer reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully"));
    }

    // ================= SEARCH REVIEWS =================
    // GET /api/admin/reviews/search?keyword=&rating=&page=0&size=10
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> searchReviews(
            @ModelAttribute SearchReviewRequest request,
            @ModelAttribute PaginationRequest pagination
    ) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.searchReviews(request, pagination)));
    }
}
