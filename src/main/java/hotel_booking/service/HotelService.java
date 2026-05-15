package hotel_booking.service;

import hotel_booking.dto.request.HotelAmenityItemRequest;
import hotel_booking.dto.request.UpdateHotelRequest;
import hotel_booking.dto.response.HotelAmenityResponse;
import hotel_booking.dto.response.HotelImageResponse;
import hotel_booking.dto.response.HotelResponse;
import hotel_booking.entity.Hotel;
import hotel_booking.entity.HotelAmenity;
import hotel_booking.entity.HotelImage;
import hotel_booking.repository.HotelAmenityRepository;
import hotel_booking.repository.HotelImageRepository;
import hotel_booking.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelImageRepository hotelImageRepository;
    private final HotelAmenityRepository hotelAmenityRepository;
    private final CloudinaryService cloudinaryService;

    public HotelResponse getHotelById() {

        Hotel hotel = hotelRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        return HotelResponse.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription())
                .starRating(hotel.getStarRating())
                .address(hotel.getAddress())
                .phoneNumber(hotel.getPhoneNumber())
                .email(hotel.getEmail())
                .mapUrl(hotel.getMapUrl())
                .createdAt(hotel.getCreatedAt())
                .updatedAt(hotel.getUpdatedAt())

                // ================= IMAGES =================
                .images(
                        hotel.getImages().stream()
                                .map(image -> HotelImageResponse.builder()
                                        .id(image.getId())
                                        .imageUrl(image.getImageUrl())
                                        .isPrimary(image.getIsPrimary())
                                        .caption(image.getCaption())
                                        .build())
                                .collect(Collectors.toList())
                )

                // ================= AMENITIES =================
                .amenities(
                        hotel.getAmenities().stream()
                                .map(a -> HotelAmenityResponse.builder()
                                        .id(a.getId())
                                        .name(a.getName())
                                        .description(a.getDescription())
                                        .build())
                                .collect(Collectors.toList())
                )

                .build();
    }

    // ================= UPDATE HOTEL =================

    public HotelResponse updateHotel( UpdateHotelRequest request) {

        Hotel hotel = hotelRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        // ================= UPDATE DATA =================

        hotel.setName(request.getName());
        hotel.setDescription(request.getDescription());
        hotel.setStarRating(request.getStarRating());
        hotel.setAddress(request.getAddress());
        hotel.setPhoneNumber(request.getPhoneNumber());
        hotel.setEmail(request.getEmail());
        hotel.setMapUrl(request.getMapUrl());

        // updatedAt sẽ tự update nhờ @PreUpdate
        Hotel updatedHotel = hotelRepository.save(hotel);

        // ================= RETURN RESPONSE =================

        return getHotelById();
    }

    // ================= ADD HOTEL IMAGE =================

    public HotelResponse uploadHotelImages(
            MultipartFile[] files
    ) {

        // ================= FIND HOTEL =================

        Hotel hotel = hotelRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        List<HotelImage> hotelImages = new ArrayList<>();

        // ================= UPLOAD EACH FILE =================

        for (MultipartFile file : files) {

            // upload to cloudinary
            String imageUrl = cloudinaryService.uploadFile1(file);

            // create entity
            HotelImage hotelImage = HotelImage.builder()
                    .hotel(hotel)
                    .imageUrl(imageUrl)
                    .isPrimary(false)
                    .caption(null)
                    .build();

            hotelImages.add(hotelImage);
        }

        // ================= SAVE ALL =================

        List<HotelImage> savedImages =
                hotelImageRepository.saveAll(hotelImages);

        // ================= RESPONSE =================

        return getHotelById();
    }

    // ================= DELETE HOTEL IMAGE =================

    public HotelResponse deleteHotelImage(Integer imageId) {

        // ================= FIND IMAGE =================

        HotelImage image = hotelImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        // ================= DELETE CLOUDINARY =================

        cloudinaryService.deleteFile(image.getImageUrl());

        // ================= DELETE DB =================

        hotelImageRepository.delete(image);

        return getHotelById();
    }

    // ================= UPDATE HOTEL IMAGE =================

    public HotelResponse updateHotelImage(
            Integer imageId,
            Boolean isPrimary,
            String caption,
            MultipartFile file
    ) {

        HotelImage image = hotelImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        // ================= UPLOAD NEW IMAGE =================

        if (file != null && !file.isEmpty()) {

            String imageUrl = cloudinaryService.uploadFile1(file);

            image.setImageUrl(imageUrl);
        }

        // ================= UPDATE OTHER FIELDS =================

        if (isPrimary != null) {
            image.setIsPrimary(isPrimary);
        }

        if (caption != null) {
            image.setCaption(caption);
        }

        HotelImage updatedImage = hotelImageRepository.save(image);

        // ================= RESPONSE =================

        return getHotelById();
    }

    // ================= ADD HOTEL Amenity =================
    public HotelResponse createAmenities(
            List<HotelAmenityItemRequest> requests
    ) {

        // ================= FIND HOTEL =================

        Hotel hotel = hotelRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        List<HotelAmenity> amenities = new ArrayList<>();

        // ================= CREATE LIST =================

        for (HotelAmenityItemRequest request : requests) {

            HotelAmenity amenity = HotelAmenity.builder()
                    .hotel(hotel)
                    .name(request.getName())
                    .description(request.getDescription())
                    .build();

            amenities.add(amenity);
        }

        // ================= SAVE ALL =================

        List<HotelAmenity> savedAmenities =
                hotelAmenityRepository.saveAll(amenities);

        // ================= RESPONSE =================

        return getHotelById();
    }

    // ================= UPDATE HOTEL Amenity =================
    public HotelResponse updateAmenity(

            Integer amenityId,
            String name,
            String description
    ) {

        HotelAmenity amenity = hotelAmenityRepository.findById(amenityId)
                .orElseThrow(() -> new RuntimeException("Hotel Amenity not found"));

        // ================= UPDATE =================

        if (name != null) {
            amenity.setName(name);
        }

        if (description != null) {
            amenity.setDescription(description);
        }

        HotelAmenity updatedAmenity = hotelAmenityRepository.save(amenity);

        // ================= RESPONSE =================

        return getHotelById();
    }

    // ================= DELETE HOTEL Amenity =================
    public HotelResponse deleteHotelAmenity(Integer amenityId) {

        // ================= FIND AMENITY =================

        HotelAmenity amenity = hotelAmenityRepository.findById(amenityId)
                .orElseThrow(() -> new RuntimeException("Amenity not found"));

        // ================= DELETE =================

        hotelAmenityRepository.delete(amenity);
        return getHotelById();
    }
}
