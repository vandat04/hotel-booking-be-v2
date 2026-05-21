package hotel_booking.mapper;

import hotel_booking.dto.request.CreateBookingRequest;
import hotel_booking.dto.response.*;
import hotel_booking.entity.*;
import hotel_booking.repository.RoomKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final RoomKeyRepository roomKeyRepository;

    public Booking toEntity(CreateBookingRequest request, RoomType roomType, User customer, BigDecimal totalAmount) {
        if (request == null) {
            return null;
        }
        return Booking.builder()
                .customer(customer)
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .customerEmail(request.getCustomerEmail())
                .roomType(roomType)
                .requestedQuantity(request.getAvailabilityRequest().getNumberOfRoom())
                .requestedCheckin(request.getAvailabilityRequest().getCheckIn())
                .requestedCheckout(request.getAvailabilityRequest().getCheckOut())
                .bookingType(request.getAvailabilityRequest().getBookingType())
                .bookingSource(request.getBookingSource())
                .status("PENDING")
                .totalAmount(totalAmount)
                .paymentStatus("UNPAID")
                .notes(request.getNotes())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public BookingResponse toResponse(Booking booking, String message) {
        if (booking == null) {
            return null;
        }
        return BookingResponse.builder()
                .bookingId(booking.getId())
                .status(booking.getStatus())
                .message(message)
                .build();
    }

    public BookingHistoryResponse toHistoryResponse(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingHistoryResponse.builder()
                .bookingId(booking.getId())
                .roomTypeName(booking.getRoomType() != null ? booking.getRoomType().getName() : null)
                .quantity(booking.getRequestedQuantity())
                .checkIn(booking.getRequestedCheckin())
                .checkOut(booking.getRequestedCheckout())
                .bookingType(booking.getBookingType())
                .status(booking.getStatus())
                .totalAmount(booking.getTotalAmount())
                .createdAt(booking.getCreatedAt())
                .build();
    }

    public BookingDetailResponse toDetailResponse(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingDetailResponse.builder()
                .bookingId(booking.getId())
                .bookingType(booking.getBookingType())
                .bookingSource(booking.getBookingSource())
                .bookingStatus(booking.getStatus())
                .totalAmount(booking.getTotalAmount())
                .paymentStatus(booking.getPaymentStatus())
                .notes(booking.getNotes())
                .createdAt(booking.getCreatedAt())

                // CUSTOMER
                .customerId(booking.getCustomer() != null ? booking.getCustomer().getId() : null)
                .customerName(booking.getCustomerName())
                .customerPhone(booking.getCustomerPhone())
                .customerEmail(booking.getCustomerEmail())

                // ROOM TYPE
                .roomTypeId(booking.getRoomType() != null ? booking.getRoomType().getId() : null)
                .roomTypeName(booking.getRoomType() != null ? booking.getRoomType().getName() : null)
                .pricePerDay(booking.getRoomType() != null ? booking.getRoomType().getPricePerDay() : null)
                .pricePerHour(booking.getRoomType() != null ? booking.getRoomType().getPricePerHour() : null)

                // REQUEST
                .requestedQuantity(booking.getRequestedQuantity())
                .requestedCheckin(booking.getRequestedCheckin())
                .requestedCheckout(booking.getRequestedCheckout())

                // ROOM SCHEDULE
                .roomSchedules(booking.getRoomSchedules() != null ? booking.getRoomSchedules().stream()
                        .map(this::mapRoomSchedule)
                        .toList() : List.of()
                )

                // PAYMENT
                .payments(booking.getPayments() != null ? booking.getPayments().stream()
                        .map(this::mapPayment)
                        .toList() : List.of()
                )

                // INVOICE
                .invoices(booking.getInvoices() != null ? booking.getInvoices().stream()
                        .map(this::mapInvoice)
                        .toList() : List.of()
                )
                .build();
    }

    public AdminBookingResponse toAdminResponse(Booking booking) {
        if (booking == null) {
            return null;
        }
        return AdminBookingResponse.builder()
                .id(booking.getId())
                .customerId(booking.getCustomer() != null ? booking.getCustomer().getId() : null)
                .customerName(booking.getCustomerName())
                .customerPhone(booking.getCustomerPhone())
                .customerEmail(booking.getCustomerEmail())
                .roomTypeId(booking.getRoomType() != null ? booking.getRoomType().getId() : null)
                .roomTypeName(booking.getRoomType() != null ? booking.getRoomType().getName() : null)
                .requestedQuantity(booking.getRequestedQuantity())
                .requestedCheckin(booking.getRequestedCheckin())
                .requestedCheckout(booking.getRequestedCheckout())
                .bookingType(booking.getBookingType())
                .bookingSource(booking.getBookingSource())
                .status(booking.getStatus())
                .totalAmount(booking.getTotalAmount())
                .paymentStatus(booking.getPaymentStatus())
                .build();
    }

    public AdminBookingDetailResponse toAdminDetailResponse(Booking booking) {
        if (booking == null) {
            return null;
        }
        User customer = booking.getCustomer();
        RoomType roomType = booking.getRoomType();

        List<AdminRoomScheduleResponse> schedules = booking.getRoomSchedules() != null ? booking.getRoomSchedules().stream()
                .map(schedule -> {
                    RoomKey roomKey = roomKeyRepository.findByRoomScheduleId(schedule.getId()).orElse(null);
                    return AdminRoomScheduleResponse.builder()
                            .roomScheduleId(schedule.getId())
                            .roomId(schedule.getRoom() != null ? schedule.getRoom().getId() : null)
                            .roomNumber(schedule.getRoom() != null ? schedule.getRoom().getRoomNumber() : null)
                            .floor(schedule.getRoom() != null ? schedule.getRoom().getFloor() : null)
                            .roomStatus(schedule.getRoom() != null ? schedule.getRoom().getStatus() : null)
                            .allocatedFor(schedule.getRoom() != null ? schedule.getRoom().getAllocatedFor() : null)
                            .startAt(schedule.getStartAt())
                            .endAt(schedule.getEndAt())
                            .status(schedule.getStatus())
                            .roomKeyId(roomKey != null ? roomKey.getId() : null)
                            .codeNumber(roomKey != null ? roomKey.getCodeNumber() : null)
                            .qrCodeData(roomKey != null ? roomKey.getQrCodeData() : null)
                            .roomKeyStatus(roomKey != null ? roomKey.getStatus() : null)
                            .activatedAt(roomKey != null ? roomKey.getActivatedAt() : null)
                            .expiredAt(roomKey != null ? roomKey.getExpiredAt() : null)
                            .build();
                }).toList() : List.of();

        List<PaymentResponse> payments = booking.getPayments() != null ? booking.getPayments().stream()
                .map(this::mapPayment).toList() : List.of();

        return AdminBookingDetailResponse.builder()
                .bookingId(booking.getId())
                .bookingType(booking.getBookingType())
                .bookingSource(booking.getBookingSource())
                .bookingStatus(booking.getStatus())
                .paymentStatus(booking.getPaymentStatus())
                .requestedQuantity(booking.getRequestedQuantity())
                .totalAmount(booking.getTotalAmount())
                .notes(booking.getNotes())
                .requestedCheckin(booking.getRequestedCheckin())
                .requestedCheckout(booking.getRequestedCheckout())
                .createdAt(booking.getCreatedAt())
                .customerId(customer != null ? customer.getId() : null)
                .customerName(booking.getCustomerName())
                .customerPhone(booking.getCustomerPhone())
                .customerEmail(booking.getCustomerEmail())
                .customerUsername(customer != null ? customer.getUsername() : null)
                .customerAvatar(customer != null ? customer.getAvatarUrl() : null)
                .customerGender(customer != null ? customer.getGender() : null)
                .customerDateOfBirth(customer != null ? customer.getDateOfBirth() : null)
                .roomTypeId(roomType != null ? roomType.getId() : null)
                .roomTypeName(roomType != null ? roomType.getName() : null)
                .roomTypeDescription(roomType != null ? roomType.getDescription() : null)
                .pricePerDay(roomType != null ? roomType.getPricePerDay() : null)
                .pricePerHour(roomType != null ? roomType.getPricePerHour() : null)
                .maxAdults(roomType != null ? roomType.getMaxAdults() : null)
                .maxChildren(roomType != null ? roomType.getMaxChildren() : null)
                .bedCount(roomType != null ? roomType.getBedCount() : null)
                .bedType(roomType != null ? roomType.getBedType() : null)
                .roomSizeM2(roomType != null ? roomType.getRoomSizeM2() : null)
                .roomSchedules(schedules)
                .payments(payments)
                .build();
    }

    private RoomScheduleDetailResponse mapRoomSchedule(RoomSchedule roomSchedule) {
        RoomKey roomKey = roomKeyRepository.findByRoomSchedule_Id(roomSchedule.getId()).orElse(null);
        return RoomScheduleDetailResponse.builder()
                .roomScheduleId(roomSchedule.getId())
                .roomId(roomSchedule.getRoom() != null ? roomSchedule.getRoom().getId() : null)
                .roomNumber(roomSchedule.getRoom() != null ? roomSchedule.getRoom().getRoomNumber() : null)
                .floor(roomSchedule.getRoom() != null ? roomSchedule.getRoom().getFloor() : null)
                .roomStatus(roomSchedule.getRoom() != null ? roomSchedule.getRoom().getStatus() : null)
                .startAt(roomSchedule.getStartAt())
                .endAt(roomSchedule.getEndAt())
                .scheduleStatus(roomSchedule.getStatus())
                .roomKeyId(roomKey != null ? roomKey.getId() : null)
                .codeNumber(roomKey != null ? roomKey.getCodeNumber() : null)
                .qrCodeData(roomKey != null ? roomKey.getQrCodeData() : null)
                .activatedAt(roomKey != null ? roomKey.getActivatedAt() : null)
                .expiredAt(roomKey != null ? roomKey.getExpiredAt() : null)
                .roomKeyStatus(roomKey != null ? roomKey.getStatus() : null)
                .build();
    }

    private PaymentResponse mapPayment(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .gatewayName(payment.getGatewayName())
                .paymentType(payment.getPaymentType())
                .status(payment.getStatus())
                .transactionReference(payment.getTransactionReference())
                .paymentDate(payment.getPaymentDate())
                .notes(payment.getNotes())
                .build();
    }

    private InvoiceResponse mapInvoice(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .customerName(invoice.getCustomerName())
                .customerEmail(invoice.getCustomerEmail())
                .customerPhone(invoice.getCustomerPhone())
                .amountPaid(invoice.getAmountPaid())
                .invoiceDescription(invoice.getInvoiceDescription())
                .issuedAt(invoice.getIssuedAt())
                .isSentEmail(invoice.getIsSentEmail())
                .build();
    }
}
