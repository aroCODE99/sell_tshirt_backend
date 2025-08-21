package com.aro.Repos;

import com.aro.Entity.OrderProduct;
import com.aro.Entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderProductsRepo extends JpaRepository<OrderProduct, Long> {

    void deleteByProduct(Products product);

    List<OrderProduct> findByProduct(Products products);
}
