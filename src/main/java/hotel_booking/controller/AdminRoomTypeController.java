package hotel_booking.controller;

import hotel_booking.dto.request.*;
import hotel_booking.dto.response.*;
import hotel_booking.service.RoomTypeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin/room-types")
@RequiredArgsConstructor
public class AdminRoomTypeController {

    private final RoomTypeService roomTypeService;

    // ================= VIEW ROOM TYPE LIST =================
    // GET /api/admin/room-types?status=1&page=0&size=10
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RoomTypeResponse>>> getAllRoomTypes(
            @RequestParam(required = false) Integer status,
            PaginationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(roomTypeService.getAllRoomTypes(status, request)));
    }

    // ================= VIEW ROOM TYPE DETAIL =================
    // GET /api/admin/room-types/{roomTypeId}
    @GetMapping("/{roomTypeId}")
    public ResponseEntity<ApiResponse<RoomTypeResponse>> getRoomTypeDetail(
            @PathVariable Integer roomTypeId
    ) {
        return ResponseEntity.ok(ApiResponse.success(roomTypeService.getRoomTypeDetail(roomTypeId)));
    }

    // ================= ADD NEW ROOM TYPE =================
    // POST /api/admin/room-types → 201 CREATED
    @PostMapping
    public ResponseEntity<ApiResponse<String>> createRoomType(
            @Valid @RequestBody CreateRoomTypeRequest request
    ) {
        roomTypeService.createRoomType(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Room type created successfully"));
    }

    // ================= UPDATE ROOM TYPE INFO =================
    // PUT /api/admin/room-types/{roomTypeId}
    @PutMapping("/{roomTypeId}")
    public ResponseEntity<ApiResponse<String>> updateRoomType(
            @PathVariable Integer roomTypeId,
            @Valid @RequestBody UpdateRoomTypeRequest request
    ) {
        roomTypeService.updateRoomType(roomTypeId, request);
        return ResponseEntity.ok(ApiResponse.success("Room type updated successfully"));
    }

    // ================= DELETE ROOM TYPE =================
    // DELETE /api/admin/room-types/{roomTypeId}
    @DeleteMapping("/{roomTypeId}")
    public ResponseEntity<ApiResponse<String>> deleteRoomType(
            @PathVariable Integer roomTypeId
    ) {
        roomTypeService.deleteRoomType(roomTypeId);
        return ResponseEntity.ok(ApiResponse.success("Room type deleted successfully"));
    }

    // ================= VIEW ROOM TYPE IMAGE LIST =================
    // GET /api/admin/room-types/{roomTypeId}/images
    @GetMapping("/{roomTypeId}/images")
    public ResponseEntity<ApiResponse<List<RoomTypeImageResponse>>> getImages(
            @PathVariable Integer roomTypeId
    ) {
        return ResponseEntity.ok(ApiResponse.success(roomTypeService.getRoomTypeImages(roomTypeId)));
    }

    // ================= ADD ROOM TYPE IMAGES =================
    // POST /api/admin/room-types/{roomTypeId}/images → 201 CREATED
    @PostMapping("/{roomTypeId}/images")
    public ResponseEntity<ApiResponse<String>> uploadImages(
            @PathVariable Integer roomTypeId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        roomTypeService.uploadRoomTypeImages(roomTypeId, files);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Images uploaded successfully"));
    }

    // ================= UPDATE ROOM TYPE IMAGE =================
    // PUT /api/admin/room-types/{roomTypeId}/images/{imageId}
    @PutMapping("/{roomTypeId}/images/{imageId}")
    public ResponseEntity<ApiResponse<String>> updateImage(
            @PathVariable Integer roomTypeId,
            @PathVariable Integer imageId,
            @RequestBody UpdateRoomTypeImageRequest request
    ) {
        roomTypeService.updateRoomTypeImage(imageId, request);
        return ResponseEntity.ok(ApiResponse.success("Image updated successfully"));
    }

    // ================= DELETE ROOM TYPE IMAGE =================
    // DELETE /api/admin/room-types/{roomTypeId}/images/{imageId}
    @DeleteMapping("/{roomTypeId}/images/{imageId}")
    public ResponseEntity<ApiResponse<String>> deleteImage(
            @PathVariable Integer roomTypeId,
            @PathVariable Integer imageId) {
        roomTypeService.deleteRoomTypeImage(imageId);
        return ResponseEntity.ok(ApiResponse.success("Image deleted successfully"));
    }

    // ================= VIEW LIST BASE ITEMS OF ROOM TYPE =================
    // GET /api/admin/room-types/{roomTypeId}/items
    @GetMapping("/{roomTypeId}/items")
    public ResponseEntity<ApiResponse<PageResponse<BaseItemInRoomTypeResponse>>> getItemsByRoomType(
            @PathVariable Integer roomTypeId,
            PaginationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(roomTypeService.getBaseItemsByRoomType(roomTypeId, request)));
    }

    // ================= ADD BASE ITEMS TO ROOM TYPE =================
    // POST /api/admin/room-types/{roomTypeId}/items → 201 CREATED
    @PostMapping("/{roomTypeId}/items")
    public ResponseEntity<ApiResponse<String>> addItemsToRoomType(
            @PathVariable Integer roomTypeId,
            @RequestBody List<RoomTypeItemRequest> request
    ) {
        roomTypeService.addBaseItemsToRoomType(roomTypeId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Items added successfully"));
    }

    // ================= DELETE BASE ITEM FROM ROOM TYPE =================
    // DELETE /api/admin/room-types/{roomTypeId}/items/{itemId}
    @DeleteMapping("/{roomTypeId}/items/{itemId}")
    public ResponseEntity<ApiResponse<String>> removeItemFromRoomType(
            @PathVariable Integer roomTypeId,
            @PathVariable Integer itemId
    ) {
        roomTypeService.removeBaseItemFromRoomType(roomTypeId, itemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from room type successfully"));
    }

    // ================= CREATE BASE ITEMS (standalone) =================
    // POST /api/admin/room-types/base-items → 201 CREATED
    @PostMapping("/base-items")
    public ResponseEntity<ApiResponse<List<BaseItemResponse>>> createBaseItemList(
            @RequestBody List<BaseItemCreateRequest> request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Base items created successfully", roomTypeService.createBaseItemList(request)));
    }

    // ================= UPLOAD BASE ITEM IMAGE =================
    // POST /api/admin/room-types/base-items/{baseItemId}/image
    @PostMapping(value = "/base-items/{baseItemId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadBaseItemImage(
            @PathVariable Integer baseItemId,
            @RequestPart("file") MultipartFile file
    ) {
        roomTypeService.uploadBaseItemImage(baseItemId, file);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Base item image uploaded successfully"));
    }

    // ================= DELETE BASE ITEM =================
    // DELETE /api/admin/room-types/base-items/{baseItemId}
    @DeleteMapping("/base-items/{baseItemId}")
    public ResponseEntity<ApiResponse<String>> deleteBaseItem(
            @PathVariable Integer baseItemId)
    {
        roomTypeService.deleteBaseItem(baseItemId);
        return ResponseEntity.ok(ApiResponse.success("Base item deleted successfully"));
    }

    // ================= UPDATE BASE ITEM INFO =================
    // PUT /api/admin/room-types/base-items/{baseItemId}
    @PutMapping("/base-items/{baseItemId}")
    public ResponseEntity<ApiResponse<String>> updateBaseItem(
            @PathVariable Integer baseItemId,
            @RequestBody UpdateBaseItemRequest request
    ) {
        roomTypeService.updateBaseItem(baseItemId, request);
        return ResponseEntity.ok(ApiResponse.success("Base item updated successfully"));
    }

    // ================= VIEW BASE ITEM LIST (paginated) =================
    // GET /api/admin/room-types/base-items
    @GetMapping("/base-items")
    public ResponseEntity<ApiResponse<PageResponse<BaseItemResponse>>> getAllBaseItems(
            PaginationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(roomTypeService.getAllBaseItems(request)));
    }
}
