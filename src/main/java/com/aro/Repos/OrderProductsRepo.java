package com.aro.Repos;

import com.aro.Entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductsRepo extends JpaRepository<OrderProduct, Long> {
}
