package hotel_booking.controller;

import hotel_booking.dto.request.GenerateInvoiceRequest;
import hotel_booking.dto.response.ApiResponse;
import hotel_booking.dto.response.InvoiceResponse;
import hotel_booking.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/invoices")
@RequiredArgsConstructor
public class AdminInvoiceController {

    private final InvoiceService invoiceService;

    // GENERATE INVOICE========================================================
    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceResponse>> generateInvoice(
            @RequestBody GenerateInvoiceRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.generateInvoice(request)));
    }

    // VIEW DETAIL=========================================================
    @GetMapping("/{invoiceId}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> viewInvoiceDetail(
            @PathVariable Integer invoiceId
    ) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.viewInvoiceDetail(invoiceId)));
    }

    // DOWNLOAD PDF=========================================================
    @GetMapping("/{invoiceId}/download")
    public ResponseEntity<byte[]> downloadInvoice(
            @PathVariable Integer invoiceId
    ) {
        byte[] pdf = invoiceService.downloadInvoicePdf(invoiceId);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=invoice.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // SEND EMAIL=========================================================
    @PostMapping("/{invoiceId}/send-email")
    public ResponseEntity<ApiResponse<String>> sendInvoiceEmail(
            @PathVariable Integer invoiceId
    ) {
        invoiceService.sendInvoiceEmail(invoiceId);
        return ResponseEntity.ok(ApiResponse.success("SEND_INVOICE_EMAIL_SUCCESS"));
    }
}
