package com.springboot.EwayBillAPI.Dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.springboot.EwayBillAPI.Entity.ItemListDetails;

import java.util.*;



@Repository
public interface EwayBillItemListDao extends JpaRepository<ItemListDetails, Long>{
    List<ItemListDetails> findByEwayBillDetailsEwbNo(long ewbNo);
}
