package com.aro.Repos;

import com.aro.Entity.OrderProduct;
import com.aro.Entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderProductsRepo extends JpaRepository<OrderProduct, Long> {

    void deleteByProductId(Long id);
}
