package com.aro.Repos;

import com.aro.Entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdersRepo extends JpaRepository<Orders, Long> {

    @Query("SELECT o FROM Orders o WHERE o.user.id = :userId ORDER BY o.orderDate DESC LIMIT 1")
    Optional<Orders> findLatestOrderByUserId(Long userId);

    Optional<Orders> findByUserId(Long userId);

    void deleteByUserId(Long user_id);
}
