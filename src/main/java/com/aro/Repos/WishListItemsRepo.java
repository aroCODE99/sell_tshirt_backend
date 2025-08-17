package com.aro.Repos;

import com.aro.Entity.WishListItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishListItemsRepo extends JpaRepository<WishListItems, Long> {
}
