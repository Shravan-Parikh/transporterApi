package com.springboot.SimBaseTrackingApi.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.SimBaseTrackingApi.Entity.TrackingData;
import java.util.List;


@Repository
public interface TrackingDao extends JpaRepository<TrackingData, String>{

    List<TrackingData> findByStatus(String status);

    List<TrackingData> findByMobileNumber(String mobileNumber);
}
