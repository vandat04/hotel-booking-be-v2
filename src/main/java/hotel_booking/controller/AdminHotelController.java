package hotel_booking.controller;

import hotel_booking.dto.request.HotelAmenityItemRequest;
import hotel_booking.dto.request.UpdateHotelRequest;
import hotel_booking.dto.response.ApiResponse;
import hotel_booking.dto.response.HotelResponse;
import hotel_booking.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin/hotel")
@RequiredArgsConstructor
public class AdminHotelController {

    private final HotelService hotelService;

    // ================= VIEW HOTEL =================
    // GET /api/admin/hotel
    @GetMapping
    public ResponseEntity<ApiResponse<HotelResponse>> getHotelById() {
        return ResponseEntity.ok(ApiResponse.success(hotelService.getHotelById()));
    }

    // ================= UPDATE HOTEL =================
    // PUT /api/admin/hotel
    @PutMapping
    public ResponseEntity<ApiResponse<HotelResponse>> updateHotel(
            @Valid @RequestBody UpdateHotelRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Hotel updated successfully", hotelService.updateHotel(request)));
    }

    // ================= UPLOAD HOTEL IMAGES =================
    // POST /api/admin/hotel/images → 201 CREATED
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<HotelResponse>> uploadHotelImages(
            @RequestParam("files") MultipartFile[] files
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Images uploaded successfully", hotelService.uploadHotelImages(files)));
    }

    // ================= UPDATE HOTEL IMAGE =================
    // PUT /api/admin/hotel/images/{imageId}
    @PutMapping(value = "/images/{imageId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<HotelResponse>> updateHotelImage(
            @PathVariable Integer imageId,
            @RequestParam(required = false) Boolean isPrimary,
            @RequestParam(required = false) String caption,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        return ResponseEntity.ok(ApiResponse.success("Image updated successfully",
                hotelService.updateHotelImage(imageId, isPrimary, caption, file)));
    }

    // ================= DELETE HOTEL IMAGE =================
    // DELETE /api/admin/hotel/images/{imageId}
    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<ApiResponse<HotelResponse>> deleteHotelImage(
            @PathVariable Integer imageId
    ) {
        return ResponseEntity.ok(ApiResponse.success("Image deleted successfully",
                hotelService.deleteHotelImage(imageId)));
    }

    // ================= ADD AMENITIES =================
    // POST /api/admin/hotel/amenities → 201 CREATED
    @PostMapping("/amenities")
    public ResponseEntity<ApiResponse<HotelResponse>> createAmenities(
            @RequestBody List<HotelAmenityItemRequest> request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Amenities added successfully", hotelService.createAmenities(request)));
    }

    // ================= UPDATE AMENITY =================
    // PUT /api/admin/hotel/amenities/{id}
    @PutMapping("/amenities/{id}")
    public ResponseEntity<ApiResponse<HotelResponse>> updateAmenity(
            @PathVariable Integer id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description
    ) {
        return ResponseEntity.ok(ApiResponse.success("Amenity updated successfully",
                hotelService.updateAmenity(id, name, description)));
    }

    // ================= DELETE AMENITY =================
    // DELETE /api/admin/hotel/amenities/{id}
    @DeleteMapping("/amenities/{amenityId}")
    public ResponseEntity<ApiResponse<HotelResponse>> deleteAmenity(
            @PathVariable Integer amenityId
    ) {
        return ResponseEntity.ok(ApiResponse.success("Amenity deleted successfully",
                hotelService.deleteHotelAmenity(amenityId)));
    }
}