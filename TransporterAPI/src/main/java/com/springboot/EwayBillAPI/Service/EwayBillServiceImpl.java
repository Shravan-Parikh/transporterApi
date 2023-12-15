package com.springboot.EwayBillAPI.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springboot.EwayBillAPI.Dao.EwayBillDetailsDao;
import com.springboot.EwayBillAPI.Dao.EwayBillItemListDao;
import com.springboot.EwayBillAPI.Dao.EwayBillUserDao;
import com.springboot.EwayBillAPI.Dao.EwayBillVehicleListDao;
import com.springboot.EwayBillAPI.Entity.EwayBillEntity;
import com.springboot.EwayBillAPI.Entity.EwayBillUsers;
import com.springboot.EwayBillAPI.Entity.ItemListDetails;
import com.springboot.EwayBillAPI.Entity.VehicleListDetails;
import com.springboot.EwayBillAPI.Response.ErrorResponse;
import com.springboot.EwayBillAPI.Response.EwayBillResponse;
import com.springboot.EwayBillAPI.Response.ItemListResponse;
import com.springboot.EwayBillAPI.Response.VehicleListResponse;

@Service
public class EwayBillServiceImpl implements EwayBillService{

    @Autowired
    EwayBillUserDao credentialsDao;

    @Autowired
    EwayBillDetailsDao ewayBillDetailsDao;

    @Autowired
    EwayBillItemListDao ewayBillItemListDao;

    @Autowired
    EwayBillVehicleListDao ewayBillVehicleListDao;

    @Override
    public Object SaveCredentials(EwayBillUsers entity) {

        credentialsDao.save(entity);
        return entity;
    }

    @Override
    public Object getEwayBill(Long ewbNo, String fromGstin, 
    String toGstin,String fromDate,String toDate){

        if(ewbNo!=null){
           Optional<EwayBillEntity> ewayBillDetails = ewayBillDetailsDao.findById(ewbNo);
           if(ewayBillDetails.isEmpty()){
            ErrorResponse error=new ErrorResponse();
            error.setErrorMessege("E-way Bill details not found for ewbNo. "+ewbNo);
            return error;
           }
           else{
            return createResponse(ewayBillDetails.get());
           }
        }
        else if(fromGstin!=null || toGstin!=null){
            if(fromDate!=null && toDate!=null){
                // Converting String to Timestamp and covering the whole range staring from
                // 12 am for the start date to 11:59 pm for the end date
                Timestamp fromTimestamp=Timestamp.valueOf(fromDate.trim() + " 00:00:00");
                Timestamp toTimestamp=Timestamp.valueOf(toDate.trim() + " 23:59:59");
                List<EwayBillEntity>  ewayBillDetails=new ArrayList<EwayBillEntity>();
                String Gstin="";
                if(fromGstin!=null){
                    ewayBillDetails= ewayBillDetailsDao.findByFromGstinAndTimestampBetween(fromGstin, 
                    fromTimestamp, toTimestamp);
                    Gstin=fromGstin;
                }
                else{
                    ewayBillDetails= ewayBillDetailsDao.findByToGstinAndTimestampBetween(toGstin, 
                    fromTimestamp, toTimestamp);
                    Gstin=toGstin;
                }
                if(ewayBillDetails.isEmpty()){
                    ErrorResponse error=new ErrorResponse();
                    error.setErrorMessege("E-way Bill details not found in specified time range for Gstin "+Gstin);
                    return error;
                }
                else{
                    List<EwayBillResponse> response=new ArrayList<EwayBillResponse>();
                    for(EwayBillEntity ewayBill: ewayBillDetails){
                        response.add(createResponse(ewayBill));
                    }
                    return response;
                }
            }
            else{
                ErrorResponse error=new ErrorResponse();
                error.setErrorMessege("Provide a time range");
                return error;
            }
        }
        return null;
    }

    public EwayBillResponse createResponse(EwayBillEntity ewayBillDetails){

        long ewbNo=ewayBillDetails.getEwbNo();

        EwayBillResponse response=new EwayBillResponse();
        ItemListResponse itemListReponse=new ItemListResponse();
        VehicleListResponse vehicleListResponse=new VehicleListResponse();

        List<ItemListResponse> itemListDetailsResponse=new ArrayList<ItemListResponse>();
        List<VehicleListResponse> vehicleListDetailsResponse=new ArrayList<VehicleListResponse>();

        List<ItemListDetails> itemListDetails = ewayBillItemListDao.findByEwayBillDetailsEwbNo(ewbNo);
        List<VehicleListDetails> vehicleListDetails =ewayBillVehicleListDao.findByEwayBillDetailsEwbNo(ewbNo);

        response.setEwbNo(ewbNo);
        response.setEwayBillDate(ewayBillDetails.getEwayBillDate());
        response.setGenMode(ewayBillDetails.getGenMode());
        response.setUserGstin(ewayBillDetails.getUserGstin());
        response.setSupplyType(ewayBillDetails.getSupplyType());
        response.setSubSupplyType(ewayBillDetails.getSubSupplyType());
        response.setDocType(ewayBillDetails.getDocType());
        response.setDocNo(ewayBillDetails.getDocNo());
        response.setDocDate(ewayBillDetails.getDocDate());
        response.setFromGstin(ewayBillDetails.getFromGstin());
        response.setFromTrdName(ewayBillDetails.getFromTrdName());
        response.setFromAddr1(ewayBillDetails.getFromAddr1());
        response.setFromAddr2(ewayBillDetails.getFromAddr2());
        response.setFromPlace(ewayBillDetails.getFromPlace());
        response.setFromPincode(ewayBillDetails.getFromPincode());
        response.setFromStateCode(ewayBillDetails.getFromStateCode());
        response.setToGstin(ewayBillDetails.getToGstin());
        response.setToTrdName(ewayBillDetails.getToTrdName());
        response.setToAddr1(ewayBillDetails.getToAddr1());
        response.setToAddr2(ewayBillDetails.getToAddr2());
        response.setToPlace(ewayBillDetails.getToPlace());
        response.setToPincode(ewayBillDetails.getToPincode());
        response.setToStateCode(ewayBillDetails.getToStateCode());
        response.setTotalValue(ewayBillDetails.getTotalValue());
        response.setTotInvValue(ewayBillDetails.getTotInvValue());
        response.setCgstValue(ewayBillDetails.getCgstValue());
        response.setSgstValue(ewayBillDetails.getSgstValue());
        response.setIgstValue(ewayBillDetails.getIgstValue());
        response.setCessValue(ewayBillDetails.getCessValue());
        response.setTransporterId(ewayBillDetails.getTransporterId());
        response.setTransporterName(ewayBillDetails.getTransporterName());
        response.setStatus(ewayBillDetails.getStatus());
        response.setActualDist(ewayBillDetails.getActualDist());
        response.setNoValidDays(ewayBillDetails.getNoValidDays());
        response.setValidUpto(ewayBillDetails.getValidUpto());
        response.setExtendedTimes(ewayBillDetails.getExtendedTimes());
        response.setRejectStatus(ewayBillDetails.getRejectStatus());
        response.setVehicleType(ewayBillDetails.getVehicleType());
        response.setActFromStateCode(ewayBillDetails.getActFromStateCode());
        response.setActToStateCode(ewayBillDetails.getActToStateCode());
        response.setTransactionType(ewayBillDetails.getTransactionType());
        response.setOtherValue(ewayBillDetails.getOtherValue());
        response.setCessNonAdvolValue(ewayBillDetails.getCessNonAdvolValue());


        for(ItemListDetails itemList:itemListDetails){

            itemListReponse.setItemNo(itemList.getItemNo());
            itemListReponse.setProductId(itemList.getProductId());
            itemListReponse.setProductName(itemList.getProductName());
            itemListReponse.setProductDesc(itemList.getProductDesc());
            itemListReponse.setHsnCode(itemList.getHsnCode());
            itemListReponse.setQuantity(itemList.getQuantity());
            itemListReponse.setQtyUnit(itemList.getQtyUnit());
            itemListReponse.setCgstRate(itemList.getCgstRate());
            itemListReponse.setSgstRate(itemList.getSgstRate());
            itemListReponse.setIgstRate(itemList.getIgstRate());
            itemListReponse.setCessRate(itemList.getCessRate());
            itemListReponse.setCessNonAdvol(itemList.getCessNonAdvol());
            itemListReponse.setTaxableAmount(itemList.getTaxableAmount());

            itemListDetailsResponse.add(itemListReponse);
        }

        for(VehicleListDetails vehicleList:vehicleListDetails){
            vehicleListResponse.setUpdMode(vehicleList.getUpdMode());
            vehicleListResponse.setVehicleNo(vehicleList.getVehicleNo());
            vehicleListResponse.setFromPlace(vehicleList.getFromPlace());
            vehicleListResponse.setFromState(vehicleList.getFromState());
            vehicleListResponse.setTripshtNo(vehicleList.getTripshtNo());
            vehicleListResponse.setUserGSTINTransin(vehicleList.getUserGSTINTransin());
            vehicleListResponse.setEnteredDate(vehicleList.getEnteredDate());
            vehicleListResponse.setTransMode(vehicleList.getTransMode());
            vehicleListResponse.setTransDocNo(vehicleList.getTransDocNo());
            vehicleListResponse.setTransDocDate(vehicleList.getTransDocDate());
            vehicleListResponse.setGroupNo(vehicleList.getGroupNo());

            vehicleListDetailsResponse.add(vehicleListResponse);
        }

        response.setItemListDetails(itemListDetailsResponse);
        response.setVehicleListDetails(vehicleListDetailsResponse);

        return response;
    }

}
