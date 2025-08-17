package com.aro.Repos;

import com.aro.Entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishListRepo extends JpaRepository<WishList, Long> {

    Optional<WishList> findByUserId(Long id);

}
