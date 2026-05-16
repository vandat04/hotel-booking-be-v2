package hotel_booking.controller;

import hotel_booking.dto.request.*;
import hotel_booking.dto.response.*;
import hotel_booking.entity.BaseItem;
import hotel_booking.service.RoomTypeService;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin/room-type")
@RequiredArgsConstructor
public class AdminRoomTypeController {

    private final RoomTypeService roomTypeService;

    // ================= VIEW ROOM TYPE LIST =================
    @GetMapping
    public ResponseEntity<PageResponse<RoomTypeResponse>> getAllRoomTypes(
            @PathParam("status") Integer status,
            PaginationRequest request
    ) {
        return ResponseEntity.ok(roomTypeService.getAllRoomTypes(status, request));
    }

    // ================= VIEW ROOM TYPE DETAIL =================
    @GetMapping("/{roomTypeId}")
    public ResponseEntity<RoomTypeResponse> getRoomTypeDetail(
            @PathVariable Integer roomTypeId
    ) {
        return ResponseEntity.ok(roomTypeService.getRoomTypeDetail(roomTypeId));
    }

    // ================= ADD NEW ROOM TYPE =================
    @PostMapping()
    public ResponseEntity<?> createRoomType(
            @RequestBody CreateRoomTypeRequest request
    ) {
        roomTypeService.createRoomType(request);
        return ResponseEntity.ok("Add New Room Type Successful");
    }

    // ================= UPDATE ROOM TYPE INFO =================
    @PutMapping("/{roomTypeId}")
    public ResponseEntity<?> updateRoomType(
            @PathVariable Integer roomTypeId,
            @Valid @RequestBody UpdateRoomTypeRequest request
    ) {
        roomTypeService.updateRoomType(roomTypeId, request);
        return ResponseEntity.ok("Update Room Type Info Successful");
    }

    // ================= DELETE ROOM TYPE =================
    @DeleteMapping("/{roomTypeId}")
    public ResponseEntity<?> deleteRoomType(
            @PathVariable Integer roomTypeId
    ) {
        roomTypeService.deleteRoomType(roomTypeId);
        return ResponseEntity.ok("DELETE_ROOM_TYPE_SUCCESS");
    }

    // ================= VIEW ROOM TYPE IMAGE LIST =================
    @GetMapping("/{roomTypeId}/images")
    public ResponseEntity<List<RoomTypeImageResponse>> getImages(
            @PathVariable Integer roomTypeId
    ) {
        return ResponseEntity.ok(roomTypeService.getRoomTypeImages(roomTypeId));
    }

    // ================= ADD ROOM TYPE IMAGE   =================
    @PostMapping("/{roomTypeId}/images")
    public ResponseEntity<?> uploadImages(
            @PathVariable Integer roomTypeId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        roomTypeService.uploadRoomTypeImages(roomTypeId, files);
        return ResponseEntity.ok("UPLOAD_SUCCESS");
    }

    // ================= UPDATE ROOM TYPE IMAGE =================
    @PutMapping("/{roomTypeId}/images/{imageId}")
    public ResponseEntity<?> updateImage(
            @PathVariable Integer roomTypeId,
            @PathVariable Integer imageId,
            @RequestBody UpdateRoomTypeImageRequest request
    ) {

        roomTypeService.updateRoomTypeImage(imageId, request);
        return ResponseEntity.ok("UPDATE_IMAGE_SUCCESS");
    }

    // ================= DELETE ROOM TYPE IMAGE =================
    @DeleteMapping("/{roomTypeId}/images/{imageId}")
    public ResponseEntity<?> deleteImage(
            @PathVariable Integer roomTypeId,
            @PathVariable Integer imageId) {
        roomTypeService.deleteRoomTypeImage(imageId);
        return ResponseEntity.ok("DELETE_IMAGE_SUCCESS");
    }

    // ================= VIEW LIST BASE ITEM OF ROOM TYPE =================
    @GetMapping("/{roomTypeId}/items")
    public ResponseEntity<PageResponse<BaseItemInRoomTypeResponse>>  getItemsByRoomType(
            @PathVariable Integer roomTypeId,
            PaginationRequest request
    ) {
        return ResponseEntity.ok(roomTypeService.getBaseItemsByRoomType(roomTypeId, request));
    }

    // ================= ADD BASE ITEM FOR ROOM TYPE =================
    @PostMapping("/{roomTypeId}/items")
    public ResponseEntity<?> addItemsToRoomType(
            @PathVariable Integer roomTypeId,
            @RequestBody List<RoomTypeItemRequest > request
    ) {
        roomTypeService.addBaseItemsToRoomType(roomTypeId, request);
        return ResponseEntity.ok("ADD_SUCCESS");
    }

    // ================= DELETE BASE ITEM OF ROOM TYPE =================
    @DeleteMapping("/{roomTypeId}/items/{itemId}")
    public ResponseEntity<?> removeItemFromRoomType(
            @PathVariable Integer roomTypeId,
            @PathVariable Integer itemId
    ) {
        roomTypeService.removeBaseItemFromRoomType(roomTypeId, itemId);
        return ResponseEntity.ok("DELETE_SUCCESS");
    }

    // ================= ADD BASE ITEM =================
    @PostMapping("/base-items")
    public ResponseEntity<?> createBaseItemList(
            @RequestBody List<BaseItemCreateRequest> request
    ) {
        return ResponseEntity.ok(roomTypeService.createBaseItemList(request));
    }

    // ================= UPLOAD BASE IMAGE =================
    @PostMapping(value = "/base-items/{baseItemId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(
            @PathVariable Integer baseItemId,
            @RequestPart("file") MultipartFile file
    ) {
        roomTypeService.uploadBaseItemImage(baseItemId, file);
        return ResponseEntity.ok("UPLOAD_SUCCESS");
    }

    // ================= DELETE BASE ITEM =================
    @DeleteMapping("/base-items/{baseItemId}")
    public ResponseEntity<?> deleteBaseItem(
            @PathVariable Integer baseItemId)
    {
        roomTypeService.deleteBaseItem(baseItemId);
        return ResponseEntity.ok("DELETE_SUCCESS");
    }

    // ================= UPDATE BASE ITEM INFO =================
    @PutMapping("/base-items/{baseItemId}")
    public ResponseEntity<?> updateBaseItem(
            @PathVariable Integer baseItemId,
            @RequestBody UpdateBaseItemRequest request
    ) {
        roomTypeService.updateBaseItem(baseItemId, request);
        return ResponseEntity.ok("UPDATE_SUCCESS");
    }

    // ================= VIEW BASE ITEM LIST =================
    @GetMapping("/base-items")
    public ResponseEntity<PageResponse<BaseItemResponse>> getAllBaseItems(
            PaginationRequest request
    ) {
        return ResponseEntity.ok(
                roomTypeService.getAllBaseItems(request)
        );
    }

    //
}
