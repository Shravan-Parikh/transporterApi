package com.springboot.EwayBillAPI.Dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.EwayBillAPI.Entity.VehicleListDetails;

@Repository
public interface EwayBillVehicleListDao extends JpaRepository<VehicleListDetails, Long>{
    List<VehicleListDetails> findByEwayBillDetailsEwbNo(long ewbNo);
}
