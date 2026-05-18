package hotel_booking.controller;


import hotel_booking.dto.request.GenerateInvoiceRequest;
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
    public InvoiceResponse generateInvoice(
            @RequestBody GenerateInvoiceRequest request
    ) {
        return invoiceService.generateInvoice(request);
    }

    // VIEW DETAIL=========================================================
    @GetMapping("/{invoiceId}")
    public InvoiceResponse viewInvoiceDetail(
            @PathVariable Integer invoiceId
    ) {
        System.out.println("heloo");
        return invoiceService.viewInvoiceDetail(invoiceId);
    }

    // DOWNLOAD PDF=========================================================
    @GetMapping("/{invoiceId}/download")
    public ResponseEntity<byte[]> downloadInvoice(
            @PathVariable Integer invoiceId
    ) {

        byte[] pdf =
                invoiceService.downloadInvoicePdf(invoiceId);

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
    public String sendInvoiceEmail(
            @PathVariable Integer invoiceId
    ) {

        invoiceService.sendInvoiceEmail(invoiceId);

        return "SEND_INVOICE_EMAIL_SUCCESS";
    }
}
