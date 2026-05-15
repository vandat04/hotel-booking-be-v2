package hotel_booking.controller;

import hotel_booking.dto.request.HotelAmenityItemRequest;
import hotel_booking.dto.request.UpdateHotelRequest;
import hotel_booking.dto.response.HotelResponse;
import hotel_booking.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin/hotel")
@RequiredArgsConstructor
public class AdminHotelController {

    private final HotelService hotelService;

    // ================= VIEW HOTEL =================
    @GetMapping()
    public HotelResponse getHotelById() {
        return hotelService.getHotelById();
    }

    // ================= UPDATE HOTEL =================
    @PutMapping()
    public HotelResponse updateHotel(
            @RequestBody UpdateHotelRequest request
    ) {
        return hotelService.updateHotel(request);
    }

    // ================= ADD IMAGE =================
    @PostMapping( value = "/img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
    public HotelResponse uploadHotelImages(
            @RequestParam("files") MultipartFile[] files
    ) {
        return hotelService.uploadHotelImages( files );
    }

    // ================= UPDATE IMAGE =================
    @PutMapping(value = "/img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HotelResponse updateHotelImage(
            @RequestParam Integer imageId,
            @RequestParam(required = false) Boolean isPrimary,
            @RequestParam(required = false) String caption,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        return hotelService.updateHotelImage( imageId, isPrimary, caption, file);
    }

    // ================= DELETE IMAGE =================
    @DeleteMapping("/img")
    public HotelResponse deleteHotelImage(
            @RequestParam Integer imageId
    ) {
        return hotelService.deleteHotelImage(imageId);
    }

    // ================= ADD Amenity =================
    @PostMapping("/amenity")
    public HotelResponse createAmenities(
            @RequestBody List<HotelAmenityItemRequest> request
    ) {
        return hotelService.createAmenities(request);
    }

    // ================= UPDATE Amenity =================
    @PutMapping("/amenity")
    public HotelResponse updateAmenity(
            @RequestParam Integer id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description
    ) {
        return hotelService.updateAmenity( id, name, description);
    }

    // ================= DELETE Amenity =================
    @DeleteMapping("/amenity")
    public HotelResponse deleteAmenity(
            @RequestParam Integer amenityId
    ) {
        return hotelService.deleteHotelAmenity(amenityId);
    }
}