package com.springboot.ShipperAPI.Dao;

import com.springboot.ShipperAPI.Entity.Shipper;
import com.springboot.ShipperAPI.Entity.ShipperTransporterEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface ShipperTransporterEmailDao extends JpaRepository<ShipperTransporterEmail, Long> {

    void deleteAllByShipper(Shipper shipper);

    ArrayList<ShipperTransporterEmail> findByShipperShipperId(String shipperId);
}
