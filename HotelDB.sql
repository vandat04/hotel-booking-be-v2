CREATE DATABASE HotelDB;
GO

USE HotelDB;
GO
--------------------------------------------------------
-- USER & AUTH MODULE
--------------------------------------------------------
CREATE TABLE Users (
    id INT IDENTITY PRIMARY KEY,
    -- Login truyền thống
    username NVARCHAR(50) NOT NULL UNIQUE,
    password_hash NVARCHAR(255),
    -- OAuth
    provider NVARCHAR(20) NOT NULL DEFAULT 'LOCAL',
    provider_id NVARCHAR(100),
    email NVARCHAR(100) NOT NULL UNIQUE,
    email_verified BIT DEFAULT 0,
    -- Profile
    full_name NVARCHAR(100),
    phone NVARCHAR(20) UNIQUE,
    avatar_url NVARCHAR(255),
    gender NVARCHAR(10) DEFAULT 'MALE' CHECK (gender IN ('MALE','FEMALE')), -- MALE, FEMALE
    date_of_birth DATE NULL,
    -- Role
    role NVARCHAR(20) NOT NULL DEFAULT 'CUSTOMER' CHECK (role IN ('ADMIN','CUSTOMER','CLEANER','RECEPTIONIST')), -- ADMIN, CUSTOMER, CLEANER, RECEPTIONIST
    -- Status
    status INT DEFAULT 1 CHECK (status IN (1,2,3)), -- 1:ACTIVE, 2:INACTIVE, 3:BANNED
    -- Audit
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);
GO
--Khi User quên mật khẩu, gửi mã OTP về cho User qua email có thời hạn xử dụng 5p hoặc trước khi hết hạn
CREATE TABLE ResetPasswordOTP (
    id INT IDENTITY PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    otp_code NVARCHAR(6) NOT NULL, -- Đổi tên cho rõ nghĩa
    expired_at DATETIME NOT NULL,
    is_used BIT DEFAULT 0,

    CONSTRAINT FK_ResetOTP_Users FOREIGN KEY (user_id)
    REFERENCES Users(id)
);
GO

CREATE TABLE InvalidTokens (
    id INT IDENTITY PRIMARY KEY,
    token NVARCHAR(500),
    expiry_time DATETIME
);
GO
--------------------------------------------------------
-- HOTEL MODULE
--------------------------------------------------------
--  Tạo bảng Hotel Lưu thông tin chi tiết của Hotel sử dụng dịch vụ
CREATE TABLE Hotel (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    star_rating FLOAT CHECK (star_rating >= 0 AND star_rating <= 5),
    address NVARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    email VARCHAR(100),
    map_url VARCHAR(255),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE()
);
-- Tạo bảng HotelImages Lưu Hình ảnh của khách sạn
CREATE TABLE HotelImages (
    id INT IDENTITY(1,1) PRIMARY KEY,
    hotel_id INT NOT NULL DEFAULT 1,
    image_url VARCHAR(MAX) NOT NULL,
    is_primary BIT DEFAULT 0,
    caption NVARCHAR(255),
    CONSTRAINT FK_HotelImages_Hotel FOREIGN KEY (hotel_id) REFERENCES Hotel(id) ON DELETE CASCADE
);
GO

-- Tạo bảng Amenities Lưu Tiện nghi thuộc về khách sạn duy nhất này
CREATE TABLE HotelAmenities (
    id INT IDENTITY(1,1) PRIMARY KEY,
    hotel_id INT NOT NULL DEFAULT 1, -- Khóa ngoại liên kết trực tiếp tới Hotel
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(255),
    CONSTRAINT FK_Amenities_Hotel FOREIGN KEY (hotel_id) REFERENCES Hotel(id) ON DELETE CASCADE
);
GO

--------------------------------------------------------
-- ROOM MODULE
--------------------------------------------------------
-- Tạo ra danh sách các loại phòng có trong Hotel
CREATE TABLE RoomTypes (
    id INT IDENTITY(1,1) PRIMARY KEY,
    hotel_id INT NOT NULL DEFAULT 1,
    name NVARCHAR(100) NOT NULL,-- STANDARD, DULEXE, VIP
    description NVARCHAR(MAX),
    status INT DEFAULT 1 CHECK (status IN (0,1)),-- 1: Còn hoạt động, 0: Ngừng

    -- --- Cấu hình giá ---
    price_per_day DECIMAL(18, 2) NOT NULL CHECK (price_per_day >= 0),-- Giá thuê theo ngày
    price_per_hour DECIMAL(18, 2) NOT NULL CHECK (price_per_hour >= 0),-- Giá giờ đầu

    -- --- Cấu hình tỷ lệ để tối ưu doanh thu (Ví dụ: 70% Ngày - 30% Giờ) ---
    -- Lưu thông tin này để làm cơ sở cho thuật toán tự động điều chỉnh của hệ thống
    target_daily_percentage INT DEFAULT 70 CHECK (target_daily_percentage BETWEEN 0 AND 100),-- Tỷ lệ phòng muốn giữ cho thuê theo ngày (%)
    target_hourly_percentage INT DEFAULT 30 CHECK (target_hourly_percentage BETWEEN 0 AND 100),-- Tỷ lệ phòng muốn giữ cho thuê theo giờ (%)

    -- Thông số vật lý
    max_adults INT,
    max_children INT,
    bed_count INT,
    bed_type NVARCHAR(50),-- SINGLE, DOUBLE, KING SIZE
    room_size_m2 FLOAT,

    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    CONSTRAINT FK_RoomTypes_Hotel FOREIGN KEY (hotel_id) REFERENCES Hotel(id) ON DELETE CASCADE,
    CHECK ( target_daily_percentage + target_hourly_percentage = 100)
);
GO
--  TẠO BẢNG RoomTypeImages (Quản lý hình ảnh của loại phòng)
CREATE TABLE RoomTypeImages (
    id INT IDENTITY(1,1) PRIMARY KEY,
    room_type_id INT NOT NULL,-- Khóa ngoại liên kết tới bảng RoomTypes
    image_url VARCHAR(MAX) NOT NULL,-- Đường dẫn lưu trữ ảnh
    is_primary BIT DEFAULT 0,-- 1: Ảnh đại diện hiển thị chính, 0: Ảnh phụ đi kèm
    caption NVARCHAR(255),-- Chú thích ảnh (Ví dụ: "Góc ban công", "Phòng tắm")
    created_at DATETIME2 DEFAULT GETDATE(),
    CONSTRAINT FK_RoomTypeImages_RoomTypes FOREIGN KEY (room_type_id) REFERENCES RoomTypes(id) ON DELETE CASCADE
);
GO
--  TẠO BẢNG BaseItems (Quản lý các vật dụng của khách sạn)
CREATE TABLE BaseItems (
    id INT IDENTITY(1,1) PRIMARY KEY,
    item_name NVARCHAR(100) NOT NULL UNIQUE,-- TIVI, TỦ LẠNH, MÁY SẤY, KHĂN TẮM, LY...
    base_unit_price DECIMAL(18, 2) NOT NULL CHECK (base_unit_price >= 0),-- Giá đền bù mặc định
    item_image_url VARCHAR(MAX) NULL,
    description NVARCHAR(255),
    created_at DATETIME2 DEFAULT GETDATE()
);
GO
--  TẠO BẢNG RoomTypeItems (Quản lý hình ảnh các vật dụng của khách sạn)
CREATE TABLE RoomTypeItems (
    room_type_id INT NOT NULL,-- Liên kết tới RoomTypes
    item_id INT NOT NULL,-- Liên kết tới BaseItems
    quantity INT DEFAULT 1 CHECK (quantity > 0),-- Số lượng định mức (Ví dụ: Phòng VIP có 4 ly, Standard có 2 ly)
    PRIMARY KEY (room_type_id, item_id),
    CONSTRAINT FK_RoomTypeItems_Types FOREIGN KEY (room_type_id) REFERENCES RoomTypes(id) ON DELETE CASCADE,
    CONSTRAINT FK_RoomTypeItems_Items FOREIGN KEY (item_id) REFERENCES BaseItems(id) ON DELETE CASCADE
);
GO
-- TẠO BẢNG Rooms (Danh sách phòng vật lý cụ thể để quản lý quỹ phòng)
CREATE TABLE Rooms (
    id INT IDENTITY(1,1) PRIMARY KEY,
    room_type_id INT NOT NULL,
    room_number VARCHAR(20) NOT NULL UNIQUE,
    floor INT DEFAULT 1,

    allocated_for NVARCHAR(20) NOT NULL DEFAULT 'DAILY' CHECK (allocated_for IN ('DAILY', 'HOURLY')),-- Mục đích phân bổ: 'DAILY', 'HOURLY'
    status NVARCHAR(50) DEFAULT N'READY' CHECK (status IN ('READY','DIRTY','MAINTENANCE')),-- Trạng thái vận hành: 'READY', 'DIRTY', 'MAINTENANCE'

    expected_checkout_at DATETIME2 NULL,-- [BỔ SUNG] Thời gian dự kiến check-out để thuật toán tự động tính toán, xoay vòng phòng trống

    is_active BIT DEFAULT 1,-- 1: Đang kinh doanh, 0: Khóa phòng hoàn toàn

    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    CONSTRAINT FK_Rooms_RoomTypes FOREIGN KEY (room_type_id) REFERENCES RoomTypes(id) ON DELETE CASCADE
);
GO

CREATE INDEX IX_Rooms_Search
ON Rooms(room_type_id, allocated_for, status);
GO

--------------------------------------------------------
-- BOOKING & PAYMENT & INVOICES
--------------------------------------------------------
-- TẠO BẢNG OTAChannels (Danh sách các API OTA để lấy dữ liệu)
CREATE TABLE OTAChannels (
    id INT IDENTITY PRIMARY KEY,
    ota_hotel_id NVARCHAR(100),
    name NVARCHAR(50), -- Agoda, Booking, Expedia
    api_key_secret NVARCHAR(255),
    webhook_secret NVARCHAR(255),
    is_active BIT DEFAULT 1
);
GO

-- TẠO BẢNG Bookings (Lưu tổng quan hóa đơn/giao dịch đặt phòng)
-- Phù hợp đa kênh: WEB, WALK-IN (Tại quầy), OTA (Agoda, Booking.com...)
CREATE TABLE Bookings (
    id INT IDENTITY(1,1) PRIMARY KEY,
    customer_id INT NULL,-- Đối với khách hàng thuê tại quầy, ota
    customer_name NVARCHAR(100) NOT NULL,-- Tên khách hàng đại diện đặt phòng
    customer_phone VARCHAR(20) NOT NULL,-- Số điện thoại khách hàng
    customer_email VARCHAR(100),

    room_type_id INT NOT NULL,
    requested_quantity INT NOT NULL CHECK (requested_quantity > 0),
    requested_checkin DATETIME2 NOT NULL,
    requested_checkout DATETIME2 NOT NULL,
    booking_type NVARCHAR(20) NOT NULL CHECK (booking_type IN ('DAILY','HOURLY')),

    booking_source NVARCHAR(50) NOT NULL DEFAULT 'WEB' CHECK ( booking_source IN ('WEB', 'WALK-IN', 'AGODA', 'BOOKING', 'EXPEDIA') ),-- Nguồn đặt phòng để thống kê tối ưu doanh thu: 'WEB', 'WALK-IN', 'AGODA', 'BOOKING', 'EXPEDIA'...

    -- Trạng thái của đơn đặt phòng:
    -- 'PENDING' (Chờ thanh toán), 'CONFIRMED' (Đã xác nhận giữ phòng), 'CHECKED_IN' (Đang ở), 'CHECKED_OUT' (Đã trả phòng), 'CANCELLED' (Đã hủy)
    status NVARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK ( status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN', 'CHECKED_DAMAGE_ROOM', 'CHECKED_OUT', 'CANCELLED', 'NO_SHOW' ) ),

    total_amount DECIMAL(18, 2) NOT NULL DEFAULT 0.00 CHECK (total_amount >= 0), -- Tổng tiền của đơn đặt phòng

    -- 'UNPAID' (Chưa thanh toán), 'PARTIALLY_PAID' (Đặt cọc một phần), 'PAID' (Đã thanh toán đủ)
    payment_status NVARCHAR(50) DEFAULT 'UNPAID' CHECK ( payment_status IN ('UNPAID','PARTIALLY_PAID','PAID', 'REFUND') ),
    notes NVARCHAR(MAX),-- Ghi chú của khách hoặc lễ tân
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    CONSTRAINT FK_Bookings_Users FOREIGN KEY (customer_id) REFERENCES Users(id),
    CONSTRAINT FK_Bookings_RoomTypes FOREIGN KEY (room_type_id) REFERENCES RoomTypes(id),
    CONSTRAINT CK_Bookings_Time CHECK (requested_checkout > requested_checkin)
);
GO

CREATE INDEX IX_Bookings_Status
ON Bookings(status, payment_status);
GO
-- TẠO BẢNG BookingExtensions (Lưu tổng gia hạn Bookings)
CREATE TABLE BookingExtensions (
    id INT IDENTITY(1,1) PRIMARY KEY,
    booking_id INT NOT NULL,-- Liên kết với đơn đặt phòng gốc

    -- Thông tin thời gian
    old_end_at DATETIME2 NOT NULL,-- Ngày/giờ trả phòng cũ (để lưu vết)
    new_end_at DATETIME2 NOT NULL,-- Ngày/giờ trả phòng mới sau khi gia hạn

    -- Thông tin tài chính phát sinh
    extension_fee DECIMAL(18, 2) NOT NULL DEFAULT 0, -- Phí gia hạn thêm (Chưa bao gồm trong Booking gốc)

    -- Trạng thái thanh toán cho đợt gia hạn này
    payment_status NVARCHAR(50) DEFAULT 'UNPAID' CHECK ( payment_status IN ( 'UNPAID', 'PAID' ) ),-- 'UNPAID', 'PAID'

    reason NVARCHAR(MAX),-- Lý do gia hạn (VD: Khách lùi chuyến bay, muốn ở thêm 2 tiếng)

    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),

    CONSTRAINT FK_Extensions_Bookings FOREIGN KEY (booking_id) REFERENCES Bookings(id) ON DELETE CASCADE,
    CONSTRAINT CK_BookingExtensions_Time CHECK (new_end_at > old_end_at)
);
GO

-- TẠO BẢNG RoomSchedules (Lịch trình phòng - Trái tim chống trùng phòng)
-- Lưu quỹ thời gian sở hữu của từng phòng vật lý theo thời gian thực (Real-time Timeline)
CREATE TABLE RoomSchedules (
    id INT IDENTITY(1,1) PRIMARY KEY,
    booking_id INT NOT NULL,-- Thuộc đơn đặt phòng nào
    room_id INT NOT NULL,-- Khóa phòng vật lý cụ thể nào trong bảng Rooms

    -- Thời gian bắt đầu và kết thúc chính xác (Bắt buộc dùng DATETIME2 để tính toán đến từng phút cho thuê theo giờ)
    start_at DATETIME2 NOT NULL,-- Ngày giờ Check-in dự kiến/thực tế
    end_at DATETIME2 NOT NULL,-- Ngày giờ Check-out dự kiến/thực tế

    -- Trạng thái của lịch trình: 'SCHEDULED' (Lên lịch đặt trước), 'ACTIVE' (Khách đang ở), 'COMPLETED' (Đã kết thúc lịch lưu trú)
    status NVARCHAR(50) NOT NULL DEFAULT 'HOLD' CHECK ( status IN ('HOLD', 'SCHEDULED', 'ACTIVE', 'COMPLETED', 'CANCELLED' ) ),

    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),

    -- Khóa ngoại
    CONSTRAINT FK_RoomSchedules_Bookings FOREIGN KEY (booking_id) REFERENCES Bookings(id) ON DELETE CASCADE,
    CONSTRAINT FK_RoomSchedules_Rooms FOREIGN KEY (room_id) REFERENCES Rooms(id) ON DELETE CASCADE,
    CONSTRAINT CK_RoomSchedules_Time CHECK (end_at > start_at)
);
GO

ALTER TABLE RoomSchedules
ADD CONSTRAINT UQ_RoomSchedules_Booking_Room UNIQUE (booking_id, room_id);
GO

-- Tạo Index tăng tốc truy vấn kiểm tra trùng lịch phòng (Rất quan trọng khi quét phòng trống)
CREATE INDEX IX_RoomSchedules_Timeline
ON RoomSchedules (room_id, start_at, end_at)
WHERE status IN ('SCHEDULED', 'ACTIVE');
GO
-- TẠO BẢNG RoomKeys (Mã số và QR Code vào phòng - Tự động sinh khi CHECK-IN)
CREATE TABLE RoomKeys (
    id INT IDENTITY(1,1) PRIMARY KEY,
    room_schedule_id INT NOT NULL UNIQUE,-- Liên kết 1-1 với lịch trình phòng đang hoạt động

    code_number VARCHAR(50) NOT NULL UNIQUE,-- Mã số PIN ngẫu nhiên gửi cho khách mở cửa (Ví dụ: '582910')
    qr_code_data VARCHAR(MAX) NOT NULL,-- Chuỗi dữ liệu mã hóa (Token/JWT) để thư viện Backend vẽ thành mã QR

    activated_at DATETIME2 NOT NULL,-- Thời điểm khóa bắt đầu có hiệu lực (Thường bằng giờ Check-in thực tế)
    expired_at DATETIME2 NOT NULL,-- Thời điểm khóa hết hiệu lực (Tự động khóa cửa sau giờ Check-out)

    -- Trạng thái khóa: 'ACTIVE' (Đang có hiệu lực), 'EXPIRED' (Đã hết hạn/Khóa bị vô hiệu hóa), 'REVOKED' (Bị hủy sớm do đổi phòng - huỷ booking)
    status NVARCHAR(50) NOT NULL DEFAULT 'ACTIVE' CHECK ( status IN ( 'ACTIVE', 'EXPIRED', 'REVOKED' ) ),

    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),

    CONSTRAINT FK_RoomKeys_RoomSchedules FOREIGN KEY (room_schedule_id) REFERENCES RoomSchedules(id) ON DELETE CASCADE,
    CONSTRAINT CK_RoomKeys_Time CHECK (expired_at > activated_at)
);
GO
-- TẠO BẢNG RoomDamages (Lưu lại những vấn đề khi Cleaner dọn phòng)
CREATE TABLE RoomDamages (
    id INT IDENTITY(1,1) PRIMARY KEY,
    booking_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT DEFAULT 1 CHECK (quantity > 0),
    actual_damage_fee DECIMAL(18,2) NOT NULL CHECK (actual_damage_fee >= 0), -- Số tiền phạt thực tế
    note NVARCHAR(MAX),
    evidence_image_url VARCHAR(MAX), -- Ảnh bằng chứng hư hại
    reported_at DATETIME2 DEFAULT GETDATE(),
    CONSTRAINT FK_Damages_Bookings FOREIGN KEY (booking_id) REFERENCES Bookings(id),
    CONSTRAINT FK_Damages_Items FOREIGN KEY (item_id) REFERENCES BaseItems(id)
);
GO
-- TẠO BẢNG Payments (Lưu lại lịch sử Thanh Toán)
CREATE TABLE Payments (
    id INT IDENTITY(1,1) PRIMARY KEY,
    booking_id INT NOT NULL,
    amount DECIMAL(18, 2) NOT NULL CHECK (amount >= 0),
    payment_method NVARCHAR(20) NOT NULL CHECK (payment_method IN ('CASH','ONLINE') ), -- 'CASH', 'ONLINE' (Chuyển khoản, Ví điện tử, Thẻ)
    gateway_name NVARCHAR(50), --VNPAY, MOMO, PAYOS, STRIPE
    -- Phân loại rõ ràng:
    -- 'FULL_ROOM_CHARGE': Thanh toán 100% tiền phòng lúc đặt.
    -- 'EXTEND_ROOM_CHARGE': Thanh toán 100% tiền phòng gia hạn.
    -- 'DAMAGE_CHARGE': Thanh toán tiền hư hại lúc check-out.
    payment_type NVARCHAR(50) NOT NULL DEFAULT 'FULL_ROOM_CHARGE' CHECK ( payment_type IN ( 'FULL_ROOM_CHARGE', 'EXTEND_ROOM_CHARGE', 'DAMAGE_CHARGE' ) ),

    status NVARCHAR(50) DEFAULT 'SUCCESS' CHECK ( status IN ( 'SUCCESS', 'FAILED', 'REFUNDED' ) ), -- Trạng thái giao dịch: 'SUCCESS' (Thành công), 'FAILED' (Thất bại), 'REFUNDED' (Đã hoàn tiền)
    transaction_reference VARCHAR(100) NULL, -- Mã MoMo, VNPAY, hoặc số biên lai
    payment_date DATETIME2 DEFAULT GETDATE(),
    notes NVARCHAR(MAX),

    CONSTRAINT FK_Payments_Bookings FOREIGN KEY (booking_id) REFERENCES Bookings(id) ON DELETE CASCADE
);
GO

CREATE INDEX IX_Payments_Booking
ON Payments(booking_id);
GO
-- TẠO BẢNG Invoices (Tạo hoá đơn)
CREATE TABLE Invoices (
    id INT IDENTITY(1,1) PRIMARY KEY,
    booking_id INT NOT NULL,
    payment_id INT NOT NULL,                        -- Hóa đơn này dành cho lần trả tiền nào?
    invoice_number AS('INV' +  RIGHT('000000' + CAST(id AS VARCHAR(10)), 6)),

    customer_name NVARCHAR(100) NOT NULL,
    customer_email VARCHAR(100) NOT NULL,
    customer_phone VARCHAR(20) NOT NULL,

    amount_paid DECIMAL(18, 2) NOT NULL CHECK (amount_paid >= 0),-- Số tiền thực tế của đợt thanh toán này

    invoice_description NVARCHAR(255),-- Ghi chú loại hóa đơn để hiển thị trong Email:  'TIỀN PHÒNG', 'TIỀN PHÒNG GIA HẠN', 'BỒI THƯỜNG VẬT DỤNG'

    issued_at DATETIME2 DEFAULT GETDATE(),
    is_sent_email BIT DEFAULT 0,-- Hệ thống Mail sẽ quét các cột 0 để gửi đi

    CONSTRAINT FK_Invoices_Bookings FOREIGN KEY (booking_id) REFERENCES Bookings(id),
    CONSTRAINT FK_Invoices_Payments FOREIGN KEY (payment_id) REFERENCES Payments(id)
);
GO

-- TẠO BẢNG Reviews (Tạo review sau khi CHECK-OUT thành công)
CREATE TABLE Reviews (
    id INT IDENTITY(1,1) PRIMARY KEY,
    booking_id INT NOT NULL UNIQUE,-- Mỗi lượt đặt phòng chỉ được đánh giá 1 lần
    customer_id INT NULL,-- Người đánh giá (liên kết với bảng Users) - hoặc không nếu booking tại quầy
    customer_name NVARCHAR(255),
    room_type_id INT NOT NULL,-- Đánh giá dành cho loại phòng nào (Standard, Deluxe...)

    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),-- Thang điểm đánh giá (Thường từ 1 đến 5 sao)

    comment NVARCHAR(MAX),-- Nội dung nhận xét

    -- Phản hồi từ phía khách sạn (Dành cho quản lý trả lời khách)
    hotel_reply NVARCHAR(MAX),
    replied_at DATETIME2 NULL,

    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),

    CONSTRAINT FK_Reviews_Bookings FOREIGN KEY (booking_id) REFERENCES Bookings(id),
    CONSTRAINT FK_Reviews_Users FOREIGN KEY (customer_id) REFERENCES Users(id),
    CONSTRAINT FK_Reviews_RoomTypes FOREIGN KEY (room_type_id) REFERENCES RoomTypes(id)
);
GO
--------------------------------------------------------
-- NOTIFICATIONS
--------------------------------------------------------
-- TẠO BẢNG CustomerNotifications (Tạo thông báo cho CUSTOMER)
CREATE TABLE CustomerNotifications (
    id INT IDENTITY(1,1) PRIMARY KEY,

    user_id INT NULL,-- Khách hàng nhận thông báo nếu có acc
    booking_id INT NULL,-- Booking liên quan (nếu có)

    -- Nội dung thông báo
    title NVARCHAR(255) NOT NULL,
    message NVARCHAR(MAX) NOT NULL,

    notification_type NVARCHAR(50) NOT NULL CHECK ( notification_type IN ( 'BOOKING_SUCCESS', 'CHECKIN_REMINDER', 'CHECKOUT_REMINDER', 'PAYMENT_SUCCESS', 'BOOKING_FAIL' , 'BOOKING_CANCEL' )),-- Loại thông báo

    -- Kênh gửi
    sent_via_email BIT DEFAULT 0,
    sent_via_app_push BIT DEFAULT 0,

    -- Trạng thái đọc
    is_read BIT DEFAULT 0,
    read_at DATETIME2 NULL,

    created_at DATETIME2 DEFAULT GETDATE(),

    CONSTRAINT FK_Notifications_Users FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    CONSTRAINT FK_Notifications_Bookings FOREIGN KEY (booking_id) REFERENCES Bookings(id)  ON DELETE SET NULL
);
GO




//



------------------------------------------------------------------------------------------------------------------------
--II. Tính lương: 
-- 1. Điểm danh mỗi ngày trong Attendance
-- 2. Lưu lỗi phạt từng ngày vào trong Penalties
-- 3. tính lương cuối ngày SalaryRecords = Attendance - Penalties
-- 4. Mỗi tháng Salaries = tổng SalaryRecords trong 1 tháng

CREATE TABLE SalaryOfRole (
    id INT IDENTITY PRIMARY KEY,
    role NVARCHAR(20) NOT NULL UNIQUE, -- ADMIN, CLEANER, RECEPTIONIST
    salary DECIMAL(12,2) NOT NULL,     -- lương cơ bản cho role
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME
);
GO

--SLOT 1: 0h-8h
--SLOT 2: 8h-16h
--SLOT 3: 16h-24h
CREATE TABLE SlotWork(
	id INT IDENTITY PRIMARY KEY,
    start_time time,
    end_time time
);
GO

CREATE TABLE Attendance (
    id INT IDENTITY PRIMARY KEY,
    user_id INT NOT NULL,
    work_date DATE, -- ngày làm
    check_in TIME,
    check_out TIME,
    status INT DEFAULT 1,  -- 1: đi làm, 2: nghỉ, 3: đi trễ
    slot_id INT,
    CONSTRAINT FK_Att_User
    FOREIGN KEY (user_id) REFERENCES Users(id),
    CONSTRAINT UQ_Attendance UNIQUE(user_id, work_date)
);
GO

ALTER TABLE Attendance
ADD CONSTRAINT FK_Attendance_Slot
FOREIGN KEY (slot_id) REFERENCES SlotWork(id);
GO

--CHECK-IN trễ, CHECK-OUT sớm : mõi lỗi phạt 100k
CREATE TABLE Penalties (
    id INT IDENTITY PRIMARY KEY,
    user_id INT NOT NULL,
    work_date DATE NOT NULL, -- ngày bị phạt
    amount DECIMAL(12,2) NOT NULL, -- số tiền phạt (luôn là số dương)
    reason NVARCHAR(255),  -- ví dụ: đi trễ, làm sai, nghỉ không phép
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Penalty_User 
    FOREIGN KEY (user_id) REFERENCES Users(id)
);
GO

CREATE TABLE SalaryRecords (
    id INT IDENTITY PRIMARY KEY,
    user_id INT NOT NULL,
    work_date DATE DEFAULT GETDATE(), -- ngày phát sinh tiền
    amount DECIMAL(12,2) NOT NULL, -- số tiền (+ hoặc -)
    type NVARCHAR(50),  -- 'WORK', 'BONUS'
    note NVARCHAR(255),
    batch_id NVARCHAR(100),
    CONSTRAINT FK_Record_User 
    FOREIGN KEY (user_id) REFERENCES Users(id)
);
GO

CREATE TABLE Salaries (
    id INT IDENTITY PRIMARY KEY,
    user_id INT,
    salary_month INT,
    salary_year INT,
    total_salary DECIMAL(12,2),
    status INT DEFAULT 1,
    attendance INT,
    -- 1: chưa trả, 2: đã trả
    CONSTRAINT FK_Salary_User 
    FOREIGN KEY (user_id) REFERENCES Users(id),
    CONSTRAINT UQ_User_Month 
    UNIQUE (user_id, salary_month, salary_year)
);
GO
-- VI. Clean Task:
-- 1. Khi đến thời gian check-out trước 1 tiếng, Khách hàng sẽ nhận thông báo check-out
-- 2. Sau khi Customer check-out, Cleaner nhận thông báo dọn phòng và lưu task vào CleaningTask
-- 3. Khi check phòng, theo danh sách RoomItem đã thiết lập, nếu có cái nào hư thì lưu vào: CheckList, lưu ảnh vào PictureEvidence
-- 4. Tính tổng tiền phạt lưu vào Booking với status: PENALTY

CREATE TABLE CleaningTasks (
    id INT IDENTITY PRIMARY KEY,
    room_id INT,
    cleaner_id INT,
    booking_id INT,
    status NVARCHAR(20), -- PENDING, DOING, DONE
    created_at DATETIME DEFAULT GETDATE(),
    completed_at DATETIME,

    FOREIGN KEY (room_id) REFERENCES Rooms(id),
    FOREIGN KEY (cleaner_id) REFERENCES Users(id),
    FOREIGN KEY (booking_id) REFERENCES Bookings(id)
);
GO

CREATE TABLE CheckList (
    id INT IDENTITY PRIMARY KEY,
    cleaning_task_id INT,
    item_id INT,
    expected_quantity INT,
    actual_quantity INT,
    amount DECIMAL(12,2), -- (expected_quantity - actual_quantity) * price trong RoomItems

    FOREIGN KEY (cleaning_task_id) REFERENCES CleaningTasks(id),
    FOREIGN KEY (item_id) REFERENCES RoomItems(id)
);
GO

CREATE TABLE PictureEvidence (
    id INT IDENTITY PRIMARY KEY,
    checklist_id INT,
    url NVARCHAR(255),

    FOREIGN KEY (checklist_id) REFERENCES CheckList(id)
);
GO

