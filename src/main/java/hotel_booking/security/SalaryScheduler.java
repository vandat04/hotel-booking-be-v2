//package hotel_booking.security;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class SalaryScheduler {
//
//    private final SalaryService salaryService;
//
//    @Scheduled(cron = "0 0 7 * * ?", zone = "Asia/Ho_Chi_Minh")
//    public void runDailySalaryJob() {
//        salaryService.calculateDailySalary();
//    }
//}
