package com.aro.Repos;

import com.aro.Entity.AppUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepo extends JpaRepository<AppUsers, Long> {

    Optional<AppUsers> findByEmail(String email);

    boolean existsByEmail(String email);
}
