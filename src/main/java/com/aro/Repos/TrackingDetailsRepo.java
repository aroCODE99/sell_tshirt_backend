package com.aro.Repos;

import com.aro.Entity.TrackingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackingDetailsRepo extends JpaRepository<TrackingDetails, Long> {
}
