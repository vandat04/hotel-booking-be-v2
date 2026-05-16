package hotel_booking.repository;

import hotel_booking.dto.request.PaginationRequest;
import hotel_booking.dto.response.BaseItemResponse;
import hotel_booking.dto.response.PageResponse;
import hotel_booking.entity.BaseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaseItemRepository extends JpaRepository<BaseItem, Integer> {

    boolean existsByItemName(String itemName);

    List<BaseItem> findByItemNameIn(List<String> names);

    boolean existsByItemNameAndIdNot(String itemName, Integer id);

}
