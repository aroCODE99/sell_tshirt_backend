package com.aro.Repos;

import com.aro.Entity.AppUsers;
import com.aro.Entity.Orders;
import com.aro.Entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentsRepo extends JpaRepository<Payments, Long> {

    Optional<Payments> findByUser(AppUsers user);

    Optional<Payments> findByOrderId(Long orderId);

}
