package hotel_booking.security;

import hotel_booking.entity.RoomKey;
import hotel_booking.repository.RoomKeyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomKeyService {

    private final RoomKeyRepository roomKeyRepository;

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void expireRoomKeys() {
        System.out.println(">>> ROOM KEY SCHEDULER RUNNING");
        LocalDateTime now = LocalDateTime.now();
        List<RoomKey> expiredKeys = roomKeyRepository.findExpiredKeys(now);
        if (expiredKeys.isEmpty()) {
            return ;
        }
        for (RoomKey key : expiredKeys) {
            key.setStatus("EXPIRED");
            key.setUpdatedAt(now);
        }
        roomKeyRepository.saveAll(expiredKeys);
        int count = expiredKeys.size();
        System.out.println("EXPIRED ROOM KEYS: " + count);
    }

}
