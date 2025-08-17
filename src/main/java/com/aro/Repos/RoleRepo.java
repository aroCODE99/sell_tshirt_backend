package com.aro.Repos;

import com.aro.Entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Roles, Long> {

    Optional<Roles> findByRoleName(String roleName);

}
