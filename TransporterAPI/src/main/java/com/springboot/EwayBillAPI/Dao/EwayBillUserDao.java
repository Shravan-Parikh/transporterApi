package com.springboot.EwayBillAPI.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.EwayBillAPI.Entity.EwayBillUsers;
import java.util.List;

@Repository
public interface EwayBillUserDao extends JpaRepository<EwayBillUsers, String> {
    
    List<EwayBillUsers>  findByRole(String role);
}
