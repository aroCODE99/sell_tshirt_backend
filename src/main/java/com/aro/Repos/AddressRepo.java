package com.aro.Repos;

import com.aro.Entity.Addresses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepo extends JpaRepository<Addresses, Long> {

    Optional<Addresses> findByAddressType(String AddressType);
}
