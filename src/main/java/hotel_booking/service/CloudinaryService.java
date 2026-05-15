package hotel_booking.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import hotel_booking.dto.response.CloudinaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadFile1(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "avatars"
                    )
            );

            return uploadResult.get("url").toString();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Upload ảnh thất bại: " + e.getMessage());
        }
    }

    public CloudinaryResponse uploadFile(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "hotel/room-types"
                    )
            );

            String url = uploadResult.get("secure_url").toString(); // ✅ https
            String publicId = uploadResult.get("public_id").toString(); // 🔥 dùng để xoá

            return new CloudinaryResponse(url, publicId);

        } catch (Exception e) {
            throw new RuntimeException("Upload ảnh thất bại: " + e.getMessage());
        }
    }

    // 👉 xoá ảnh
    public void deleteFile(String publicId) {
        try {
            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.emptyMap()
            );
        } catch (Exception e) {
            throw new RuntimeException("Xoá ảnh thất bại: " + e.getMessage());
        }
    }

}
