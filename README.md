# Tổng hợp nghiệp vụ hệ thống quản lý khách sạn thông minh
Hệ thống được thiết kế theo mô hình:
* Smart Hotel PMS (Property Management System)
* Quản lý 1 khách sạn
* Hỗ trợ:
    * Thuê theo ngày
    * Thuê theo giờ
    * OTA
    * Walk-in
    * Smart room key
    * Notification
    * Multi-payment
    * Timeline chống overbooking

---

# I. MỤC TIÊU HỆ THỐNG
Hệ thống phục vụ các nhu cầu:
* Quản lý vận hành khách sạn
* Quản lý đặt phòng đa kênh
* Chống trùng phòng
* Tối ưu doanh thu giữa thuê giờ và thuê ngày
* Hỗ trợ check-in/check-out thông minh
* Hỗ trợ thanh toán online
* Hỗ trợ vận hành realtime

---

# II. ĐỐI TƯỢNG SỬ DỤNG
## 1. CUSTOMER
Khách hàng đặt phòng:
* Đăng ký tài khoản
* Đăng nhập
* Đặt phòng
* Thanh toán
* Nhận thông báo
* Review

---

## 2. RECEPTIONIST
Lễ tân:
* Tạo booking tại quầy
* Check-in/check-out
* Gán phòng
* Gia hạn phòng
* Quản lý thanh toán
* Quản lý khách lưu trú

---

## 3. CLEANER
Nhân viên dọn phòng:
* Xem phòng DIRTY
* Cập nhật trạng thái READY

---

## 4. ADMIN
Quản trị hệ thống:
* Quản lý khách sạn
* Quản lý loại phòng
* Quản lý OTA
* Quản lý quỹ phòng
* Theo dõi doanh thu
* Theo dõi booking/payment/review

---

# III. USER & AUTH MODULE
# 1. Đăng ký tài khoản
Khách hàng có thể:
* đăng ký bằng email/password
* hoặc OAuth
Hệ thống lưu:
* username
* email
* phone
* avatar
* profile

---

# 2. Đăng nhập
Hỗ trợ:
* LOCAL LOGIN
* GOOGLE LOGIN
* FACEBOOK LOGIN (mở rộng)

---

# 3. Quên mật khẩu
Luồng:
1. User nhập email
2. Hệ thống tạo OTP
3. OTP hết hạn sau 5 phút
4. User reset password

---

# 4. Logout JWT
JWT bị đưa vào blacklist:
* InvalidTokens

---

# IV. HOTEL MODULE
# 1. Quản lý thông tin khách sạn
Admin quản lý:
* tên khách sạn
* mô tả
* hotline
* email
* địa chỉ
* map

---

# 2. Quản lý hình ảnh khách sạn
* nhiều ảnh
* ảnh đại diện

---

# 3. Quản lý tiện nghi
Ví dụ:
* Wifi
* Hồ bơi
* Gym
* Bãi đỗ xe

---

# V. ROOM MODULE
# 1. Quản lý loại phòng
Ví dụ:
* Standard
* Deluxe
* VIP

---

## Mỗi loại phòng gồm:
### Thông tin hiển thị
* tên
* mô tả
* diện tích
* số giường
* loại giường
* sức chứa

---

## Giá
* giá theo ngày
* giá theo giờ

---

## Tối ưu doanh thu
Hệ thống hỗ trợ:
* phân bổ quỹ phòng giữa DAILY và HOURLY
Ví dụ:
* 70% cho thuê ngày
* 30% cho thuê giờ
Đây là nền tảng cho:
* smart allocation engine

---

# 2. Quản lý hình ảnh phòng
* album phòng
* ảnh đại diện

---

# 3. Quản lý vật dụng phòng
## Danh mục vật dụng
Ví dụ:
* TV
* khăn
* ly
* máy sấy

---

## Cấu hình vật dụng theo loại phòng
Ví dụ:
* Standard có 2 khăn
* VIP có minibar

---

# 4. Quản lý phòng vật lý
Ví dụ:
* P101
* P102

---

## Trạng thái phòng
### READY
Sẵn sàng bán
### DIRTY
Cần dọn
### MAINTENANCE
Bảo trì

---

## Phân bổ phòng
### DAILY
Cho thuê ngày
### HOURLY
Cho thuê giờ

---

# VI. BOOKING MODULE
# 1. Đặt phòng online
Khách hàng:
1. Chọn loại phòng
2. Chọn thời gian
3. Chọn số lượng phòng
4. Thanh toán
5. Nhận xác nhận booking

---

# 2. Walk-in booking
Lễ tân:
1. Tạo booking tại quầy
2. Gán phòng trực tiếp
3. Check-in ngay

---

# 3. OTA booking
Hệ thống hỗ trợ OTA:
* Agoda
* Booking
* Expedia

---

## Luồng OTA
1. OTA gửi booking
2. Hệ thống tạo booking
3. Allocate phòng
4. Đồng bộ trạng thái

---

# 4. Booking status lifecycle
## PENDING
Chờ thanh toán
## CONFIRMED
Đã giữ phòng
## CHECKED_IN
Khách đang ở
## CHECKED_OUT
Đã trả phòng
## CANCELLED
Đã hủy
## NO_SHOW
Khách không đến

---

# VII. ROOM SCHEDULING ENGINE
# RoomSchedules = lõi hệ thống
Đây là:
* timeline quản lý phòng thực tế

---

# Vai trò chính
## Chống overbooking
Kiểm tra:
* room_id
* start_at
* end_at

---

## Quản lý booking nhiều phòng
1 booking:
* có thể chứa nhiều phòng

---

## Quản lý trạng thái lưu trú
### SCHEDULED
Đặt trước
### ACTIVE
Đang ở
### COMPLETED
Đã hoàn tất
### CANCELLED
Đã hủy

---

# VIII. CHECK-IN / CHECK-OUT FLOW
# 1. Check-in
Lễ tân:
1. Xác nhận khách tới
2. Gán phòng
3. Booking → CHECKED_IN
4. RoomSchedule → ACTIVE
5. Sinh khóa QR/PIN

---

# 2. Check-out
1. Kiểm tra hư hại
2. Tính phí phát sinh
3. Thanh toán còn thiếu
4. Booking → CHECKED_OUT
5. Room → DIRTY
6. RoomSchedule → COMPLETED

---

# IX. SMART ROOM KEY SYSTEM
# RoomKeys
Tự động sinh:
* PIN code
* QR code

---

# Trạng thái khóa
## ACTIVE
Đang dùng
## EXPIRED
Hết hạn
## REVOKED
Bị hủy

---

# X. PAYMENT MODULE
# 1. Thanh toán
Hỗ trợ:
* CASH
* ONLINE

---

# Gateway online
* VNPAY
* MOMO
* PAYOS
* STRIPE

---

# 2. Multi-payment
Một booking có thể:
* thanh toán nhiều lần
Ví dụ:
* đặt cọc
* thanh toán gia hạn
* thanh toán hư hại

---

# 3. Loại thanh toán
## FULL_ROOM_CHARGE
Tiền phòng
## EXTEND_ROOM_CHARGE
Gia hạn
## DAMAGE_CHARGE
Bồi thường

---

# 4. Payment status
## SUCCESS
## FAILED
## REFUNDED

---

# XI. BOOKING EXTENSION
Khách có thể:
* gia hạn giờ/ngày lưu trú
Hệ thống lưu:
* thời gian cũ
* thời gian mới
* phí gia hạn

---

# XII. DAMAGE MANAGEMENT
# RoomDamages
Quản lý:
* hư hại
* mất đồ
* đền bù

---

# Hỗ trợ:
* ảnh bằng chứng
* phí thực tế
* số lượng hư hỏng

---

# XIII. INVOICE MODULE
# Invoices
Sinh hóa đơn cho:
* thanh toán phòng
* gia hạn
* bồi thường

---

# Hỗ trợ
* email invoice
* PDF invoice
* lưu lịch sử

---

# XIV. REVIEW MODULE
Sau checkout:
* khách được đánh giá

---

# Hỗ trợ
## Rating
1 → 5 sao
## Comment
Nội dung review
## Hotel reply
Khách sạn phản hồi

---

# XV. CUSTOMER NOTIFICATION MODULE
# CustomerNotifications
Hệ thống gửi thông báo cho khách đã có tài khoản.

---

# Các loại notification
## BOOKING_SUCCESS
Đặt phòng thành công
## CHECKIN_REMINDER
Nhắc check-in
## CHECKOUT_REMINDER
Nhắc check-out
## PAYMENT_SUCCESS
Thanh toán thành công
## CANCEL_SUCCESS
Hủy booking thành công

---

# Kênh gửi
## Email
## App/Web Push

---

# Trạng thái
## Read / Unread
Hỗ trợ:
* badge notification
* realtime notification center

---

# XVI. CLEANER WORKFLOW
# Luồng dọn phòng
1. Room → DIRTY
2. Cleaner nhận việc
3. Dọn phòng
4. Room → READY

---

# XVII. KIẾN TRÚC NGHIỆP VỤ QUAN TRỌNG
# 1. Tách Booking và RoomSchedules
## Booking
= giao dịch tổng
## RoomSchedules
= timeline chiếm dụng phòng
Đây là thiết kế chuẩn PMS thực tế.

---

# 2. Tách Room.status và RoomSchedule.status
## Room.status
Trạng thái vận hành vật lý
## RoomSchedule.status
Trạng thái lưu trú

---

# 3. Smart allocation
Hệ thống có khả năng:
* tối ưu doanh thu
* điều chỉnh quỹ phòng DAILY/HOURLY

---

# XVIII. ĐÁNH GIÁ HỆ THỐNG HIỆN TẠI
Hệ thống hiện tại đã đạt mức:
## Smart Mini PMS
Có các thành phần gần giống hệ thống thực tế:
* OTA-ready
* Timeline anti-overbooking
* Smart room allocation
* Hourly booking
* QR/PIN smart lock
* Multi-payment
* Invoice system
* Notification center
* Damage workflow
* Cleaner workflow
* Review system


