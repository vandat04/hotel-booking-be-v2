package hotel_booking.controller;

import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.request.ReplyReviewRequest;
import hotel_booking.dto.request.SearchReviewRequest;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.dto.response.ReviewResponse;
import hotel_booking.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    // ================= GET ALL REVIEW =================
    @GetMapping
    public PageResponse<ReviewResponse> getAllReviews(
            PaginationRequest request,
            @RequestParam("replyStatus") String replyStatus
    ) {
        return reviewService.getAllReviews(request, replyStatus);
    }

    // ================= GET REVIEW DETAIL =================
    @GetMapping("/detail/{reviewId}")
    public ReviewResponse getReviewDetail(@PathVariable Integer reviewId) {
        return reviewService.getReviewDetail(reviewId);
    }

    // ================= REPLY REVIEW =================
    @PutMapping("/{reviewId}/reply")
    public String replyReview(
            @PathVariable Integer reviewId,
            @RequestBody ReplyReviewRequest request
    ) {
        reviewService.replyReview(reviewId, request);
        return "Reply review success";
    }

    // ================= DELETE REVIEW =================
    @DeleteMapping("/{reviewId}")
    public String deleteReview(@PathVariable Integer reviewId) {
        reviewService.deleteReview(reviewId);
        return "Delete review success";
    }

    // ================= SEARCH REVIEW =================
    @GetMapping("/search")
    public PageResponse<ReviewResponse> searchReviews(
            @ModelAttribute SearchReviewRequest request,
            @ModelAttribute PaginationRequest pagination
    ) {
        return reviewService.searchReviews(request, pagination);
    }

}
