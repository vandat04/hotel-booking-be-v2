//package hotel_booking.controller;
//
//import hotel_booking.dto.response.UserProfileResponse;
//import hotel_booking.security.CustomUserDetails;
//import hotel_booking.service.PaymentService;
//import hotel_booking.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/hotel/payment")
//@RequiredArgsConstructor
//public class PaymentController {
//
//    private final PaymentService paymentService;
//    private final UserService userService;
//
//    @PostMapping("/vnpay")
//    public String createPayment(
//            @RequestBody PaymentRequest req,
//            @AuthenticationPrincipal CustomUserDetails user
//    ) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//        // 🔥 lấy userId từ principal (tuỳ bạn set trong JWT)
//        Long userId = Long.parseLong(auth.getName());
//
//        UserProfileResponse userp = userService.getMyProfile();
//
//        return paymentService.createVnPayPayment(userp.getId().longValue(), req);
//    }
//
//    @GetMapping("/vnpay-return")
//    public void vnpayReturn(@RequestParam Map<String, String> params, HttpServletResponse response) throws java.io.IOException {
//        paymentService.handleVnPayReturn(params);
//        // Sau khi xử lý thanh toán xong ở Backend, chuyển hướng trình duyệt về lại Frontend
//        response.sendRedirect("http://127.0.0.1:5500/hotel-booking-fe/profile.html");
//    }
//}
