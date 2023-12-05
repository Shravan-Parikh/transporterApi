package com.springboot.EwayBillAPI.Dao;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.EwayBillAPI.Entity.EwayBillEntity;

@Repository
public interface EwayBillDetailsDao extends JpaRepository<EwayBillEntity, Long> {

    List<EwayBillEntity> findByFromGstinAndTimestampBetween(String fromGstin, Timestamp fromTimestamp, Timestamp toTimestamp);

    List<EwayBillEntity> findByToGstinAndTimestampBetween(String fromGstin, Timestamp fromTimestamp, Timestamp toTimestamp);
    
}
