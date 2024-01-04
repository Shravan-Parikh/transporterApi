package com.springboot.EwayBillAPI.Service;


import com.springboot.EwayBillAPI.Entity.EwayBillEntity;
import com.springboot.EwayBillAPI.Entity.EwayBillUserRequest;
import com.springboot.EwayBillAPI.Entity.EwayBillUsers;

public interface EwayBillService {
    
    public Object SaveCredentials(EwayBillUsers entity);
    
    public Object getEwayBill(Long ewbNo,String userId, String fromGstin,
    String toGstin, String transporterGstin, String fromDate,String toDate);

    public Object updateEwayBillUser(String userId, EwayBillUserRequest requestEntity);

    public Object getUserDetails(String userId);
}
