package hotel_booking.controller;


import hotel_booking.dto.request.PaymentSearchRequest;
import hotel_booking.dto.request.UpdatePaymentStatusRequest;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.dto.response.PaymentDetailResponse;
import hotel_booking.dto.response.PaymentResponse;
import hotel_booking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/payments")
@RequiredArgsConstructor
public class AdminPaymentController {

    private final PaymentService paymentService;

    // ================= VIEW PAYMENT LIST =================
    @GetMapping
    public PageResponse<PaymentResponse> viewPaymentList(
            @ModelAttribute PaymentSearchRequest request
    ) {
        return paymentService.viewPaymentList(request);
    }

    // ================= VIEW PAYMENT DETAIL =================
    @GetMapping("/{paymentId}")
    public PaymentDetailResponse viewPaymentDetail(
            @PathVariable Integer paymentId
    ) {
        return paymentService.viewPaymentDetail(paymentId);
    }


    // ================= UPDATE PAYMENT STATUS =================
    @PutMapping("/{paymentId}/status")
    public String updatePaymentStatus(
            @PathVariable Integer paymentId,
            @RequestBody UpdatePaymentStatusRequest request
    ) {
        paymentService.updatePaymentStatus(paymentId, request);
        return "UPDATE_PAYMENT_STATUS_SUCCESS";
    }

    // ================= UPDATE PAYMENT STATUS =================
}
