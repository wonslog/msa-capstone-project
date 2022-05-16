package team.infra;

import team.domain.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderDetailsRepository extends CrudRepository<OrderDetails, Long> {

    List<OrderDetails> findByorderId(Long orderId);
    List<OrderDetails> findByorderId(Long orderId);
    List<OrderDetails> findByorderId(Long orderId);
    List<OrderDetails> findByorderId(Long orderId);
    List<OrderDetails> findByorderId(Long orderId);

}