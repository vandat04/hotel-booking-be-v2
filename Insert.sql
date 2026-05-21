USE HotelDB;
GO
-- =========================================
-- USERS SEED DATA
-- Password for all accounts: 123123
-- BCrypt hash: $2a$10$OwEVq/j00c/HhMfHUkJ9JuMVCZGv3XrmXYCWApMqtFO1HdHWN.SdG
-- =========================================
INSERT INTO Users (username, password_hash, provider, email, email_verified, full_name, phone, avatar_url, gender, date_of_birth, role, status, created_at, updated_at)
VALUES
('admin_user', '$2a$10$OwEVq/j00c/HhMfHUkJ9JuMVCZGv3XrmXYCWApMqtFO1HdHWN.SdG','LOCAL', 'admin@example.com', 1, 'Admin User', '0901000001', 'https://example.com/avatar/admin.png', 'MALE', '1995-01-01', 'ADMIN', 1, GETDATE(), GETDATE()),
('customer_user', '$2a$10$OwEVq/j00c/HhMfHUkJ9JuMVCZGv3XrmXYCWApMqtFO1HdHWN.SdG', 'LOCAL', 'customer@example.com', 1, 'Customer User', '0901000002', 'https://example.com/avatar/customer.png', 'FEMALE', '2000-05-15', 'CUSTOMER', 1, GETDATE(), GETDATE()),
('cleaner_user1', '$2a$10$OwEVq/j00c/HhMfHUkJ9JuMVCZGv3XrmXYCWApMqtFO1HdHWN.SdG', 'LOCAL', 'cleaner@example.com', 1, 'Cleaner User A', '0901000003', 'https://example.com/avatar/cleaner.png', 'MALE', '1999-05-25', 'CLEANER', 1, GETDATE(), GETDATE()),
('cleaner_user2', '$2a$10$OwEVq/j00c/HhMfHUkJ9JuMVCZGv3XrmXYCWApMqtFO1HdHWN.SdG', 'LOCAL', 'cleaner2@example.com', 1, 'Cleaner User B', '0901000004', 'https://example.com/avatar/cleaner.png', 'FEMALE', '1998-08-20', 'CLEANER', 1, GETDATE(), GETDATE()),
('reception_user1', '$2a$10$OwEVq/j00c/HhMfHUkJ9JuMVCZGv3XrmXYCWApMqtFO1HdHWN.SdG', 'LOCAL', 'reception1@example.com', 1, 'Receptionist User A', '0901000005', 'https://example.com/avatar/reception.png', 'MALE', '1997-11-10', 'RECEPTIONIST', 1, GETDATE(), GETDATE()),
('reception_user2', '$2a$10$OwEVq/j00c/HhMfHUkJ9JuMVCZGv3XrmXYCWApMqtFO1HdHWN.SdG', 'LOCAL', 'reception2@example.com', 1, 'Receptionist User B', '0901000006', 'https://example.com/avatar/reception.png', 'FEMALE', '2000-01-01', 'RECEPTIONIST', 1, GETDATE(), GETDATE());
GO

-- ==========================================
-- HOTEL
-- ==========================================
-- 1. Chèn thông tin khách sạn duy nhất
INSERT INTO Hotel (name, star_rating, address, phone_number, email) VALUES
(N'Khách sạn Bình Minh', 0, N'123 Đường Trần Hưng Đạo, Đà Nẵng', '02363123456', 'contact@binhminhhotel.com');

-- 2. Chèn hình ảnh trực tiếp cho khách sạn (hotel_id = 1)
INSERT INTO HotelImages (hotel_id, image_url, is_primary, caption) VALUES
(1, 'https://res.cloudinary.com/do8uakd0l/image/upload/v1774416942/hotel/room-types/pmdhqiqd5pvwuv7wc7pm.jpg', 1, N'Ảnh mặt tiền khách sạn'),
(1, 'https://res.cloudinary.com/do8uakd0l/image/upload/v1774416942/hotel/room-types/pmdhqiqd5pvwuv7wc7pm.jpg', 0, N'Khu vực hồ bơi');

-- 3. Chèn các tiện nghi trực tiếp cho khách sạn (hotel_id = 1)
INSERT INTO HotelAmenities (hotel_id, name, description) VALUES
(1, N'Wifi miễn phí', N'Wifi tốc độ cao toàn khuôn viên'),
(1, N'Hồ bơi vô cực', N'Hồ bơi ngoài trời tầng thượng'),
(1, N'Phòng Gym', N'Phòng tập đầy đủ máy móc hiện đại');
GO

-- ==========================================
-- ROOM MODULE
-- ==========================================
INSERT INTO RoomTypes
(name, description, status, price_per_day, price_per_hour, target_daily_percentage, target_hourly_percentage, max_adults, max_children, bed_count, bed_type, room_size_m2) VALUES
-- Loại phòng STANDARD: Tỷ lệ 50% Ngày - 50% Giờ
(N'STANDARD', N'Phòng tiêu chuẩn, tiện nghi cơ bản đầy đủ, phù hợp cho cặp đôi hoặc khách đi công tác ngắn ngày.', 1, 400000.00, 120000.00, 50, 50, 2, 1, 1, N'SINGLE', 20.5),
-- Loại phòng DELUXE: Tỷ lệ 70% Ngày - 30% Giờ (Ưu tiên bán theo ngày vì phòng đẹp hướng phố)
(N'DELUXE', N'Phòng sang trọng không gian rộng rãi, nội thất cao cấp, có cửa sổ lớn hướng nhìn ra phố.', 1, 750000.00, 180000.00, 70, 30, 2, 1, 1, N'DOUBLE', 32.0),
-- Loại phòng VIP: Tỷ lệ 100% Ngày - 0% Giờ (Dòng phòng cao cấp, chỉ bán theo ngày để tối ưu trải nghiệm dịch vụ)
(N'VIP', N'Phòng thượng hạng không gian đẳng cấp, có phòng khách riêng biệt, bồn tắm sục jacuzzi và hướng biển toàn cảnh.', 1, 1500000.00, 0.00, 100, 0, 4, 2, 2, N'KING SIZE', 60.0);
GO

INSERT INTO RoomTypeImages (room_type_id, image_url, is_primary, caption) VALUES
-- Ảnh cho phòng STANDARD
(1, 'https://res.cloudinary.com/do8uakd0l/image/upload/v1779341218/Screenshot_2026-05-21_122631_j586vn.png', 1, N'Không gian tổng thể phòng Standard'),
-- Ảnh cho phòng DELUXE
(2, 'https://res.cloudinary.com/do8uakd0l/image/upload/v1779341303/Screenshot_2026-05-21_122803_yttutl.png', 1, N'Phòng Deluxe hướng phố ban ngày'),
(2, 'https://res.cloudinary.com/do8uakd0l/image/upload/v1779341346/Screenshot_2026-05-21_122850_mklaae.png', 0, N'Phòng tắm kính hiện đại phòng Deluxe'),
-- Ảnh cho phòng VIP
(3, 'https://res.cloudinary.com/do8uakd0l/image/upload/v1779341388/Screenshot_2026-05-21_122932_egqu3o.png', 1, N'Phòng khách sang trọng phòng VIP'),
(3, 'https://res.cloudinary.com/do8uakd0l/image/upload/v1779341431/Screenshot_2026-05-21_123015_gba4ar.png', 0, N'Chi tiết giường ngủ King Size phòng VIP');
GO

-- Chèn vật dụng vào danh mục tổng
INSERT INTO BaseItems (item_name, base_unit_price) VALUES
(N'LY THỦY TINH', 50000.00),
(N'GỐI NHUNG', 200000.00),
(N'TIVI 4K', 8000000.00);

-- 2. Kết nối và định mức số lượng cho từng loại phòng
-- Phòng STANDARD: 2 Ly, 2 Gối
INSERT INTO RoomTypeItems (room_type_id, item_id, quantity) VALUES
(1, 1, 2),
(1, 2, 2),
(3, 1, 4),
(3, 2, 4),
(3, 3, 1);

INSERT INTO Rooms (room_type_id, room_number, floor, allocated_for, status, expected_checkout_at, is_active) VALUES
(1, 'S101', 1, 'DAILY', N'READY', NULL, 1), -- Phòng trống, sẵn sàng đón khách thuê theo NGÀY
(1, 'S103', 1, 'HOURLY', N'READY', NULL, 1), -- Phòng trống, sẵn sàng đón khách thuê theo GIỜ
(2, 'D201', 2, 'DAILY', N'READY', NULL, 1),
(2, 'D203', 2, 'DAILY', N'DIRTY', NULL, 1),
(2, 'D204', 2, 'HOURLY', N'READY', NULL, 1),
(3, 'V301', 3, 'DAILY', N'READY', NULL, 1),
(3, 'V302', 3, 'DAILY', N'MAINTENANCE', NULL, 0);
GO

--------------------------------------------------------
-- BOOKING & PAYMENT & INVOICES
--------------------------------------------------------
INSERT INTO Bookings ( customer_id, customer_name, customer_phone, customer_email,  room_type_id, requested_quantity, requested_checkin, requested_checkout, booking_type, booking_source,  status, total_amount,  payment_status,  notes, created_at, updated_at) VALUES
 (1, N'Nguyễn Văn A', '0901234567', 'nguyenvana@email.com', 1, 1, '2024-05-10 14:00:00', '2024-05-12 12:00:00', 'DAILY', 'AGODA', 'CHECKED_OUT', 2500000.00,'PAID', N'Khách hàng hài lòng, đã thanh toán qua thẻ.', '2024-05-01 10:00:00', '2024-05-12 12:15:00' );
GO

INSERT INTO Payments ( booking_id, amount, payment_method, gateway_name, payment_type, status, transaction_reference, payment_date, notes) VALUES
 (1, 2500000.00, 'ONLINE', 'VNPAY', 'FULL_ROOM_CHARGE', 'SUCCESS', 'VNPAY123456789', GETDATE(), N'Khách thanh toán đủ qua ứng dụng ngân hàng.');
GO

INSERT INTO Reviews ( booking_id, customer_id, customer_name, room_type_id, rating, comment, hotel_reply, replied_at, created_at) VALUES
( 1, 1, N'Nguyễn Văn A', 1, 5, N'Phòng rất sạch sẽ, nhân viên lễ tân nhiệt tình. Sẽ quay lại lần sau!', N'Cảm ơn anh A đã tin tưởng sử dụng dịch vụ của khách sạn. Hẹn gặp lại anh sớm nhất!', GETDATE(), GETDATE());
GO

INSERT INTO RoomSchedules (booking_id, room_id, start_at, end_at, status, created_at, updated_at) VALUES
(1, 1, '2024-05-10 14:00:00', '2024-05-12 12:00:00', 'SCHEDULED', '2024-05-01 10:00:00', '2024-05-12 12:15:00' );
GO

--------------------------------------------------------
-- SALARY - ATTENDANCE
--------------------------------------------------------
INSERT INTO Shifts (shift_name, start_time, end_time, description, is_active) VALUES
( N'DAY SHIFT', '00:00:01', '12:00:00', N'Ca ngày 12 giờ', 1),
( N'NIGHT SHIFT','12:00:00', '23:59:00',N'Ca đêm (tạm 6 tiếng - DB không hỗ trợ qua ngày)', 1);
GO

INSERT INTO RoleSalaryConfig ( staff_role, base_salary, is_active, created_at) VALUES
('CLEANER', 6000000, 1, GETDATE()),
('RECEPTIONIST', 8000000, 1, GETDATE());
GO






