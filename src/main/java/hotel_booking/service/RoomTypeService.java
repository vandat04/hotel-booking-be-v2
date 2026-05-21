package hotel_booking.service;

import hotel_booking.dto.request.*;
import hotel_booking.dto.response.*;
import hotel_booking.entity.*;
import hotel_booking.repository.*;
import hotel_booking.util.PaginationUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomTypeService {
    private final RoomTypeRepository roomTypeRepository;
    private final RoomTypeImageRepository imageRepository;
    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;
    private final RoomTypeImageRepository roomTypeImageRepository;
    private final CloudinaryService cloudinaryService;
    private final BaseItemRepository baseItemRepository;
    private final RoomDamageRepository roomDamageRepository;
    private final RoomTypeItemRepository roomTypeItemRepository;
    private final hotel_booking.mapper.RoomTypeMapper roomTypeMapper;

    public Page<RoomTypeListResponse> getActiveRoomTypes(PaginationRequest request) {
        Pageable pageable = PaginationUtil.build(request);
        Page<RoomType> roomTypes = roomTypeRepository.findByStatus(1, pageable);

        return roomTypes.map(roomType -> {
            // ===== THUMBNAIL =====
            String thumbnail = null;
            RoomTypeImage primary = imageRepository.findFirstByRoomTypeIdAndIsPrimaryTrue(roomType.getId()).orElse(null);

            if (primary != null) {
                thumbnail = primary.getImageUrl();
            } else {
                RoomTypeImage first = imageRepository.findFirstByRoomTypeIdOrderByIdAsc(roomType.getId()).orElse(null);
                if (first != null) {
                    thumbnail = first.getImageUrl();
                }
            }

            // ===== MAP RESPONSE =====
            return roomTypeMapper.toTypeListResponse(roomType, thumbnail);
        });
    }

    // ==================================
    // ========= CREATE ROOM TYPE  =========
    // ==================================
    public RoomType createRoomType(CreateRoomTypeRequest request) {

        // ===== CHECK HOTEL =====
        Hotel hotel = hotelRepository.findById(1).orElseThrow(() -> new RuntimeException("HOTEL_NOT_FOUND"));

        // ===== CHECK DUPLICATE =====
        boolean existed = roomTypeRepository.existsByHotelIdAndNameIgnoreCase(1, request.getName());
        if (existed) {
            throw new RuntimeException("ROOM_TYPE_ALREADY_EXISTS");
        }

        // ===== VALIDATE TARGET =====
        int total = request.getTargetDailyPercentage() + request.getTargetHourlyPercentage();

        if (total != 100) {
            throw new RuntimeException("TARGET_PERCENTAGE_MUST_EQUAL_100");
        }

        // ===== CREATE =====
        RoomType roomType = roomTypeMapper.toEntity(request);
        roomType.setHotel(hotel);

        return roomTypeRepository.save(roomType);
    }

    // ==================================
    // ========= UPDATE ROOM TYPE  =========
    // ==================================
    @Transactional
    public RoomType updateRoomType(Integer roomTypeId, UpdateRoomTypeRequest request) {

        // ===== FIND ROOM TYPE =====
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new RuntimeException("ROOM_TYPE_NOT_FOUND"));

        // ===== CHECK HOTEL =====
        Hotel hotel = hotelRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("HOTEL_NOT_FOUND"));

        // ===== CHECK DUPLICATE NAME =====
        boolean existed = roomTypeRepository.existsByHotelIdAndNameIgnoreCase(1, request.getName());

        // Nếu tên mới trùng với room type khác
        if (existed && !roomType.getName().equalsIgnoreCase(request.getName())) {
            throw new RuntimeException("ROOM_TYPE_ALREADY_EXISTS");
        }

        // ===== VALIDATE TARGET =====
        int total = request.getTargetDailyPercentage() + request.getTargetHourlyPercentage();

        if (total != 100) {
            throw new RuntimeException("TARGET_PERCENTAGE_MUST_EQUAL_100");
        }

        // ===== VALIDATE =====
        if (request.getMaxAdults() != null && request.getMaxAdults() < 1) {
            throw new RuntimeException("MAX_ADULT_INVALID");
        }

        if (request.getBedCount() != null && request.getBedCount() < 1) {
            throw new RuntimeException("BED_COUNT_INVALID");
        }

        // ===== UPDATE =====
        roomTypeMapper.updateEntity(request, roomType);
        roomType.setHotel(hotel);

        // ===== SAVE =====
        return roomTypeRepository.save(roomType);
    }

    // ==================================
    // ========= DELETE ROOM TYPE  =========
    // ==================================
    @Transactional
    public void deleteRoomType(Integer roomTypeId) {

        // ===== FIND ROOM TYPE =====
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new RuntimeException("ROOM_TYPE_NOT_FOUND"));

        // ===== CHECK BOOKING EXISTS =====
        boolean hasBooking = bookingRepository.existsByRoomTypeId(roomTypeId);

        // ===== CASE 1: NEVER BOOKED -> DELETE =====
        if (!hasBooking) {
            roomTypeRepository.delete(roomType);
            return;
        }

        // ===== CASE 2: HAS BOOKING -> SOFT DELETE =====
        roomType.setStatus(0);
        roomType.setUpdatedAt(LocalDateTime.now());
        roomTypeRepository.save(roomType);
    }

    // ==================================
    // ========= VIEW ROOM TYPE LIST  =========
    // ==================================
    public PageResponse<RoomTypeResponse> getAllRoomTypes(Integer status, PaginationRequest request) {

        Pageable pageable = PaginationUtil.build(request);
        if (status == null) {
            status = 1;
        }
        Page<RoomType> pageData = roomTypeRepository.findAllByStatus(status, pageable);
        List<RoomTypeResponse> content =
                pageData.getContent()
                        .stream()
                        .map(roomTypeMapper::toResponse)
                        .toList();

        return PageResponse.<RoomTypeResponse>builder()
                .content(content)
                .page(pageData.getNumber())
                .size(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .last(pageData.isLast())
                .build();
    }

    // ==================================
    // ========= VIEW ROOM TYPE DETAIL  =========
    // ==================================
    public RoomTypeResponse getRoomTypeDetail(Integer roomTypeId) {

        // ===== FIND ROOM TYPE =====
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new RuntimeException("ROOM_TYPE_NOT_FOUND"));

        // ===== MAP IMAGES =====
        List<String> images = (roomType.getImages() == null)
                ? List.of()
                : roomType.getImages()
                .stream()
                .map(RoomTypeImage::getImageUrl) // giả sử field này
                .toList();

        // ===== BUILD RESPONSE =====
        return roomTypeMapper.toResponse(roomType);
    }

    // ==================================
    // ========= ADD IMAGE FOR ROOM TYPE  =========
    // ==================================
    @Transactional
    public void uploadRoomTypeImages(
            Integer roomTypeId,
            List<MultipartFile> files
    ) {

        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new RuntimeException("ROOM_TYPE_NOT_FOUND"));

        if (files == null || files.isEmpty()) {
            throw new RuntimeException("NO_FILES_UPLOADED");
        }

        List<RoomTypeImage> images = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            // upload to cloudinary
            String url = cloudinaryService.uploadFile1(file);
            RoomTypeImage image = RoomTypeImage.builder()
                    .roomType(roomType)
                    .imageUrl(url)
                    .isPrimary(i == 0)
                    .createdAt(LocalDateTime.now())
                    .build();
            images.add(image);
        }
        roomTypeImageRepository.saveAll(images);
    }

    // ==================================
    // ========= UPDATE IMAGE INFOR FOR ROOM TYPE  =========
    // ==================================
    @Transactional
    public void updateRoomTypeImage(
            Integer imageId,
            UpdateRoomTypeImageRequest request
    ) {
        // ===== FIND IMAGE =====
        RoomTypeImage image = roomTypeImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("IMAGE_NOT_FOUND"));

        // ===== UPDATE CAPTION =====
        if (request.getCaption() != null) {
            image.setCaption(request.getCaption());
        }

        // ===== HANDLE PRIMARY IMAGE =====
        if (Boolean.TRUE.equals(request.getIsPrimary())) {

            // remove old primary of same roomType
            List<RoomTypeImage> images = roomTypeImageRepository.findByRoomTypeId(image.getRoomType().getId());

            for (RoomTypeImage img : images) {
                img.setIsPrimary(false);
            }
            image.setIsPrimary(true);
            roomTypeImageRepository.saveAll(images);
        }

        // ===== SAVE CURRENT IMAGE =====
        roomTypeImageRepository.save(image);
    }

    // ==================================
    // ========= VIEW ROOM TYPE IMAGE LIST  =========
    // ==================================
    public List<RoomTypeImageResponse> getRoomTypeImages(Integer roomTypeId) {

        // ===== CHECK ROOM TYPE EXISTS =====
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new RuntimeException("ROOM_TYPE_NOT_FOUND"));

        // ===== GET IMAGES =====
        List<RoomTypeImage> images = roomTypeImageRepository.findByRoomTypeIdOrderByIsPrimaryDescIdAsc(roomTypeId);

        // ===== MAP TO RESPONSE =====
        return images.stream()
                .map(img -> RoomTypeImageResponse.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .isPrimary(img.getIsPrimary())
                        .caption(img.getCaption())
                        .build()
                )
                .toList();
    }

    // ==================================
    // ========= VIEW ROOM TYPE IMAGE LIST  =========
    // ==================================
    @Transactional
    public void deleteRoomTypeImage(Integer imageId) {

        // ===== FIND IMAGE =====
        RoomTypeImage image = roomTypeImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("IMAGE_NOT_FOUND"));

        Integer roomTypeId = image.getRoomType().getId();

        boolean isPrimary = Boolean.TRUE.equals(image.getIsPrimary());

        // ===== DELETE FROM CLOUDINARY =====
        cloudinaryService.deleteFile(image.getImageUrl());

        // ===== DELETE DB =====
        roomTypeImageRepository.delete(image);

        // ===== IF PRIMARY → SET NEW PRIMARY =====
        if (isPrimary) {
            List<RoomTypeImage> images = roomTypeImageRepository.findByRoomTypeId(roomTypeId);

            if (!images.isEmpty()) {
                RoomTypeImage newPrimary = images.get(0);
                newPrimary.setIsPrimary(true);
                roomTypeImageRepository.save(newPrimary);
            }
        }
    }

    // ==================================
    // ========= ADD BASE ITEM  =========
    // ==================================
    @Transactional
    public List<BaseItemResponse> createBaseItemList(List<BaseItemCreateRequest> request) {

        if (request == null || request.isEmpty()) {
            throw new IllegalArgumentException("Item list cannot be empty");
        }

        // ===== CHECK DUPLICATE IN DB =====
        List<String> names = request
                .stream()
                .map(BaseItemCreateRequest::getItemName)
                .toList();

        List<BaseItem> existing = baseItemRepository.findByItemNameIn(names);

        if (!existing.isEmpty()) {
            throw new hotel_booking.exception.DuplicateDataException("One or more item names already exist");
        }

        // ===== MAP TO ENTITY =====
        List<BaseItem> entities = request.stream()
                .map(item -> BaseItem.builder()
                        .itemName(item.getItemName())
                        .baseUnitPrice(item.getBaseUnitPrice())
                        .description(item.getDescription())
                        .createdAt(LocalDateTime.now())
                        .build()
                )
                .toList();

        // ===== SAVE ALL AND MAP TO DTO =====
        return baseItemRepository.saveAll(entities).stream()
                .map(item -> BaseItemResponse.builder()
                        .id(item.getId())
                        .itemName(item.getItemName())
                        .baseUnitPrice(item.getBaseUnitPrice())
                        .description(item.getDescription())
                        .itemImageUrl(item.getItemImageUrl())
                        .build()
                )
                .toList();
    }

    // ==================================
    // ========= UPLOAD BASE IMAGE  =========
    // ==================================
    @Transactional
    public void uploadBaseItemImage(Integer baseItemId, MultipartFile file) {

        // ===== FIND ITEM =====
        BaseItem item = baseItemRepository.findById(baseItemId)
                .orElseThrow(() -> new RuntimeException("BASE_ITEM_NOT_FOUND"));

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("IMAGE_REQUIRED");
        }

        // ===== IF EXIST IMAGE → DELETE OLD =====
        if (item.getItemImageUrl() != null) {
            cloudinaryService.deleteFile(item.getItemImageUrl());
        }

        // ===== UPLOAD NEW IMAGE =====
        String imageUrl = cloudinaryService.uploadFile1(file);

        // ===== UPDATE ENTITY =====
        item.setItemImageUrl(imageUrl);

        baseItemRepository.save(item);
    }

    // ==================================
    // ========= DELETE BASE ITEM  =========
    // ==================================
    @Transactional
    public void deleteBaseItem(Integer id) {

        BaseItem item = baseItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BASE_ITEM_NOT_FOUND"));

        // ===== CHECK ROOM DAMAGES =====
        if (roomDamageRepository.existsByItemId(id)) {
            throw new RuntimeException("BASE_ITEM_HAS_DAMAGE_RECORD");
        }

        // ===== CHECK ROOM TYPE ITEM (nếu có) =====
        if (item.getRoomTypeItems() != null && !item.getRoomTypeItems().isEmpty()) {
            throw new RuntimeException("BASE_ITEM_IS_IN_USE");
        }

        // ===== DELETE IMAGE CLOUDINARY =====
        if (item.getItemImageUrl() != null) {
            cloudinaryService.deleteFile(item.getItemImageUrl());
        }

        // ===== DELETE DB =====
        baseItemRepository.delete(item);
    }

    // ==================================
    // ========= UPDATE BASE ITEM INFO  =========
    // ==================================
    @Transactional
    public void updateBaseItem(Integer id, UpdateBaseItemRequest request) {

        // ===== FIND ITEM =====
        BaseItem item = baseItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BASE_ITEM_NOT_FOUND"));

        // ===== CHECK DUPLICATE NAME =====
        if (request.getItemName() != null &&
                baseItemRepository.existsByItemNameAndIdNot(request.getItemName(), id)) {
            throw new RuntimeException("ITEM_NAME_ALREADY_EXISTS");
        }

        // ===== UPDATE FIELDS (KHÔNG UPDATE IMAGE) =====
        if (request.getItemName() != null) {
            item.setItemName(request.getItemName());
        }
        if (request.getBaseUnitPrice() != null) {
            item.setBaseUnitPrice(request.getBaseUnitPrice());
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }

        // ===== SAVE =====
        baseItemRepository.save(item);
    }

    // ==================================
    // ========= VIEW BASE ITEM LIST  =========
    // ==================================
    public PageResponse<BaseItemResponse> getAllBaseItems(PaginationRequest request) {

        // ===== BUILD PAGEABLE =====
        Pageable pageable = PaginationUtil.build(request);

        // ===== QUERY DB =====
        Page<BaseItem> page = baseItemRepository.findAll(pageable);

        // ===== MAP ENTITY -> DTO =====
        List<BaseItemResponse> content = page.getContent().stream()
                .map(item -> BaseItemResponse.builder()
                        .id(item.getId())
                        .itemName(item.getItemName())
                        .baseUnitPrice(item.getBaseUnitPrice())
                        .itemImageUrl(item.getItemImageUrl())
                        .description(item.getDescription())
                        .build()
                )
                .toList();

        // ===== BUILD RESPONSE =====
        return PageResponse.<BaseItemResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    // ==================================
    // ========= VIEW BASE ITEM LIST OF ROOM TYPE  =========
    // ==================================
    public PageResponse<BaseItemInRoomTypeResponse> getBaseItemsByRoomType(
            Integer roomTypeId,
            PaginationRequest request
    ) {

        // ===== CHECK ROOM TYPE EXISTS =====
        if (!roomTypeRepository.existsById(roomTypeId)) {
            throw new RuntimeException("ROOM_TYPE_NOT_FOUND");
        }

        // ===== BUILD PAGEABLE =====
        Pageable pageable = PaginationUtil.build(request);

        // ===== QUERY =====
        Page<RoomTypeItem> page = roomTypeItemRepository.findByRoomTypeId(roomTypeId, pageable);

        // ===== MAP DATA =====
        List<BaseItemInRoomTypeResponse> content = page.getContent().stream()
                .map(rti -> BaseItemInRoomTypeResponse.builder()
                        .itemId(rti.getItem().getId())
                        .itemName(rti.getItem().getItemName())
                        .itemImageUrl(rti.getItem().getItemImageUrl())
                        .baseUnitPrice(rti.getItem().getBaseUnitPrice())
                        .quantity(rti.getQuantity())
                        .build()
                )
                .toList();

        // ===== RESPONSE =====
        return PageResponse.<BaseItemInRoomTypeResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    // ==================================
    // =========  ADD BASE ITEM FOR ROOM TYPE =========
    // ==================================
    @Transactional
    public void addBaseItemsToRoomType(Integer roomTypeId, List<RoomTypeItemRequest> request) {

        // ===== CHECK ROOM TYPE =====
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new RuntimeException("ROOM_TYPE_NOT_FOUND"));

        for (RoomTypeItemRequest itemReq : request) {
            // ===== CHECK ITEM =====
            BaseItem item = baseItemRepository.findById(itemReq.getItemId())
                    .orElseThrow(() -> new RuntimeException("BASE_ITEM_NOT_FOUND"));

            // ===== CHECK EXISTING MAPPING =====
            Optional<RoomTypeItem> existing = roomTypeItemRepository
                    .findByRoomTypeIdAndItemId(roomTypeId, itemReq.getItemId());

            if (existing.isPresent()) {
                // ===== UPDATE QUANTITY =====
                RoomTypeItem rti = existing.get();
                rti.setQuantity(itemReq.getQuantity());
                roomTypeItemRepository.save(rti);

            } else {
                // ===== CREATE NEW MAPPING =====
                RoomTypeItem newItem = RoomTypeItem.builder()
                        .id(new RoomTypeItemId(roomTypeId, itemReq.getItemId()))
                        .roomType(roomType)
                        .item(item)
                        .quantity(itemReq.getQuantity())
                        .build();

                roomTypeItemRepository.save(newItem);
            }
        }
    }

    // ==================================
    // =========  DELETE BASE ITEM OF ROOM TYPE =========
    // ==================================
    @Transactional
    public void removeBaseItemFromRoomType(Integer roomTypeId, Integer itemId) {

        // ===== CHECK ROOM TYPE EXISTS =====
        if (!roomTypeRepository.existsById(roomTypeId)) {
            throw new RuntimeException("ROOM_TYPE_NOT_FOUND");
        }

        // ===== CHECK BASE ITEM EXISTS =====
        if (!baseItemRepository.existsById(itemId)) {
            throw new RuntimeException("BASE_ITEM_NOT_FOUND");
        }

        // ===== BUILD COMPOSITE KEY =====
        RoomTypeItemId id = new RoomTypeItemId(roomTypeId, itemId);

        // ===== CHECK EXISTING RELATION =====
        RoomTypeItem rti = roomTypeItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ROOM_TYPE_ITEM_NOT_FOUND"));

        // ===== DELETE =====
        roomTypeItemRepository.delete(rti);
    }

    // ==================================
    // =========  STATISTIC RATE ROOM TYPE =========
    // ==================================
    public List<RoomTypeBookingStatsResponse> getBookingStats(BookingDashboardRequest request) {

        Integer year = request.getYear();
        Integer month = request.getMonth();

        // ===== ACTIVE ROOM TYPES =====
        List<RoomType> roomTypes = roomTypeRepository.findByStatus(1);

        // ===== ONLY CHECKED_OUT + PAID =====
        List<Object[]> rawStats =
                bookingRepository.countPaidCheckedOutByRoomType(year, month);

        Map<Integer, Long> countMap = rawStats.stream()
                .collect(Collectors.toMap(
                        r -> (Integer) r[0],
                        r -> (Long) r[1]
                ));

        long total = countMap.values().stream()
                .mapToLong(Long::longValue)
                .sum();

        if (total == 0) total = 1;

        long finalTotal = total;

        return roomTypes.stream()
                .map(rt -> {
                    Long count = countMap.getOrDefault(rt.getId(), 0L);

                    return RoomTypeBookingStatsResponse.builder()
                            .roomTypeId(rt.getId())
                            .roomTypeName(rt.getName())
                            .bookingCount(count)
                            .percentage((count * 100.0) / finalTotal)
                            .build();
                })
                .toList();
    }
}
