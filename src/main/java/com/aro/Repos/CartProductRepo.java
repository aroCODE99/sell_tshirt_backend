package com.aro.Repos;

import com.aro.Entity.CartProduct;
import com.aro.Entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartProductRepo extends JpaRepository<CartProduct, Long> {

    Optional<CartProduct> findByProductId(Long id);

    void deleteByProductId(Long id);

}
