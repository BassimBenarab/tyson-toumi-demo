package com.tysontoumi.repository;

import com.tysontoumi.model.OrderStatus;
import com.tysontoumi.model.WatchOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchOrderRepository extends JpaRepository<WatchOrder, Long> {
    List<WatchOrder> findByStatus(OrderStatus status);
    List<WatchOrder> findByCustomerId(Long customerId);
}
