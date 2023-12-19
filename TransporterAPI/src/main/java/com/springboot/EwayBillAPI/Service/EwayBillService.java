package com.springboot.EwayBillAPI.Service;


import com.springboot.EwayBillAPI.Entity.EwayBillUsers;

public interface EwayBillService {
    
    public Object SaveCredentials(EwayBillUsers entity);
    
    public Object getEwayBill(Long ewbNo, String fromGstin, 
    String toGstin, String transporterGstin, String fromDate,String toDate);
}
