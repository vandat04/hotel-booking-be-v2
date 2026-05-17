package hotel_booking.service;

import hotel_booking.entity.User;
import hotel_booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    public void sendOTP(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Reset Password OTP");
        message.setText("Mã OTP của bạn là: " + otp + "\nHết hạn sau 5 phút.");

        mailSender.send(message);
    }

    // ================= SEND SIMPLE EMAIL =================
    public void sendCustomerEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }
}
