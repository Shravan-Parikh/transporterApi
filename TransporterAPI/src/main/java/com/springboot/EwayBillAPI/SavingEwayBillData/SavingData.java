package com.springboot.EwayBillAPI.SavingEwayBillData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.springboot.EwayBillAPI.Authentication.EwayTokenGenerator;
import com.springboot.EwayBillAPI.Dao.EwayBillUserDao;
import com.springboot.EwayBillAPI.Dao.EwayBillDetailsDao;
import com.springboot.EwayBillAPI.Dao.EwayBillItemListDao;
import com.springboot.EwayBillAPI.Dao.EwayBillVehicleListDao;
import com.springboot.EwayBillAPI.Entity.EwayBillUsers;
import com.springboot.EwayBillAPI.Entity.EwayBillEntity;
import com.springboot.EwayBillAPI.Entity.ItemListDetails;
import com.springboot.EwayBillAPI.Entity.VehicleListDetails;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SavingData {
    
    @Autowired
    public EwayTokenGenerator ewayTokenGenerator;

    @Autowired
    public EwayBillUserDao credentialsDao;

    @Autowired
    public EwayBillDetailsDao ewayBillDetailsDao;

    @Autowired
    public EwayBillItemListDao itemListDao;

    @Autowired
    public EwayBillVehicleListDao vehicleListDao;

    @Value("${EwayBillAccessToken}")
    String accessToken;

    @Value("${GetEwayBillDetailsByDateUrl}")
    String getEwayBillDetailsByDateUrl;

    @Value("${GetEwayBillDetailsByEwbNo}")
    String getEwayBillDetailsByEwbNoUrl;

    @Async
    public void savingEwayBillData(EwayBillUsers credentialsData) throws URISyntaxException, IOException{
        
        // First generating the authToken and sek then using it to get details by date
        ewayTokenGenerator.generateToken(credentialsData.getUsername(), credentialsData.getPassword(), credentialsData.getGstin());
        URL weburl=new URL(getEwayBillDetailsByDateUrl+LocalDate.now(ZoneId.of("Asia/Kolkata")).minusDays(1).toString());
        String authString="Bearer "+accessToken;
        HttpURLConnection webConnection = (HttpURLConnection) weburl.openConnection();
        webConnection.setRequestMethod("GET");
        webConnection.setRequestProperty("Accept", "application/json");
        webConnection.setRequestProperty("Content-Type", "application/json");
        webConnection.setRequestProperty("Authorization", authString);
        webConnection.setRequestProperty("gstin", credentialsData.getGstin());
        webConnection.setRequestProperty("authtoken", ewayTokenGenerator.getAuthToken());
        webConnection.setRequestProperty("sek", ewayTokenGenerator.getSek());
        webConnection.setDoOutput(true);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(webConnection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder resp = new StringBuilder();
            String respLine = null;
            while ((respLine = br.readLine()) != null) {
                resp.append(respLine.trim());
            }    
            JSONArray detailsByDateArray;
            // If an E-way Bill details exists, we are initializing the JSONArray variable with it
            try{
                detailsByDateArray = new JSONArray(resp.toString()); 
            }catch(Exception e){
                detailsByDateArray=new JSONArray();
            }

            for(int i=0;i<detailsByDateArray.length();i++){
                JSONObject detailsByDateObject=detailsByDateArray.getJSONObject(i);
                // Now we are fetching every eway bill number and getting the details with it then saving it to our Database
                Long ewbNo=detailsByDateObject.getLong("ewbNo");
                if(ewayBillDetailsDao.findById(ewbNo).isEmpty()){
                    weburl=new URL(getEwayBillDetailsByEwbNoUrl+Long.toString(ewbNo));
                    webConnection = (HttpURLConnection) weburl.openConnection();
                    webConnection.setRequestMethod("GET");
                    webConnection.setRequestProperty("Accept", "application/json");
                    webConnection.setRequestProperty("Content-Type", "application/json");
                    webConnection.setRequestProperty("Authorization", authString);
                    webConnection.setRequestProperty("gstin", credentialsData.getGstin());
                    webConnection.setRequestProperty("authtoken", ewayTokenGenerator.getAuthToken());
                    webConnection.setRequestProperty("sek", ewayTokenGenerator.getSek());
                    webConnection.setDoOutput(true);

                    try (BufferedReader br2 = new BufferedReader(
                        new InputStreamReader(webConnection.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder resp2 = new StringBuilder();
                        String respLine2 = null;
                        while ((respLine2 = br2.readLine()) != null) {
                            resp2.append(respLine2.trim());
                        } 
                        JSONObject detailsByEwbNo=new JSONObject(resp2.toString());

                        String ewayBillDate=detailsByEwbNo.getString("ewayBillDate");
                        String genMode=detailsByEwbNo.getString("genMode");
                        String userGstin=detailsByEwbNo.getString("userGstin");
                        String supplyType=detailsByEwbNo.getString("supplyType");
                        String subSupplyType=detailsByEwbNo.getString("subSupplyType");
                        String docType=detailsByEwbNo.getString("docType");
                        String docNo=detailsByEwbNo.getString("docNo");
                        String docDate=detailsByEwbNo.getString("docDate");
                        String fromGstin=detailsByEwbNo.getString("fromGstin");
                        String fromTrdName=detailsByEwbNo.getString("fromTrdName");
                        String fromAddr1=detailsByEwbNo.getString("fromAddr1");
                        String fromAddr2=detailsByEwbNo.getString("fromAddr2");
                        String fromPlace=detailsByEwbNo.getString("fromPlace");
                        long fromPincode=detailsByEwbNo.getLong("fromPincode");
                        long fromStateCode=detailsByEwbNo.getLong("fromStateCode");
                        String toGstin=detailsByEwbNo.getString("toGstin");
                        String toTrdName=detailsByEwbNo.getString("toTrdName");
                        String toAddr1=detailsByEwbNo.getString("toAddr1");
                        String toAddr2=detailsByEwbNo.getString("toAddr2");
                        String toPlace=detailsByEwbNo.getString("toPlace");
                        long toPincode=detailsByEwbNo.getLong("toPincode");
                        long toStateCode=detailsByEwbNo.getLong("toStateCode");
                        double totalValue=detailsByEwbNo.getDouble("totalValue");
                        double totInvValue=detailsByEwbNo.getDouble("totInvValue");
                        double cgstValue=detailsByEwbNo.getDouble("cgstValue");
                        double sgstValue=detailsByEwbNo.getDouble("sgstValue");
                        double igstValue=detailsByEwbNo.getDouble("igstValue");
                        double cessValue=detailsByEwbNo.getDouble("cessValue");
                        String transporterId=detailsByEwbNo.getString("transporterId");
                        String transporterName=detailsByEwbNo.getString("transporterName");
                        String status=detailsByEwbNo.getString("status");
                        long actualDist=detailsByEwbNo.getLong("actualDist");
                        long noValidDays=detailsByEwbNo.getLong("noValidDays");
                        String validUpto=detailsByEwbNo.getString("validUpto");
                        long extendedTimes=detailsByEwbNo.getLong("extendedTimes");
                        String rejectStatus=detailsByEwbNo.getString("rejectStatus");
                        String vehicleType=detailsByEwbNo.getString("vehicleType");
                        long actFromStateCode=detailsByEwbNo.getLong("actFromStateCode");
                        long actToStateCode=detailsByEwbNo.getLong("actToStateCode");
                        long transactionType=detailsByEwbNo.getLong("transactionType");
                        double otherValue=detailsByEwbNo.getDouble("otherValue");
                        double cessNonAdvolValue=detailsByEwbNo.getDouble("cessNonAdvolValue");

                        EwayBillEntity ewayBillData=new EwayBillEntity();

                        ewayBillData.setEwbNo(ewbNo);
                        ewayBillData.setEwayBillDate(ewayBillDate);
                        ewayBillData.setGenMode(genMode);
                        ewayBillData.setUserGstin(userGstin);
                        ewayBillData.setSupplyType(supplyType);
                        ewayBillData.setSubSupplyType(subSupplyType);
                        ewayBillData.setDocType(docType);
                        ewayBillData.setDocNo(docNo);
                        ewayBillData.setDocDate(docDate);
                        ewayBillData.setFromGstin(fromGstin);
                        ewayBillData.setFromTrdName(fromTrdName);
                        ewayBillData.setFromAddr1(fromAddr1);
                        ewayBillData.setFromAddr2(fromAddr2);
                        ewayBillData.setFromPlace(fromPlace);
                        ewayBillData.setFromPincode(fromPincode);
                        ewayBillData.setFromStateCode(fromStateCode);
                        ewayBillData.setToGstin(toGstin);
                        ewayBillData.setToTrdName(toTrdName);
                        ewayBillData.setToAddr1(toAddr1);
                        ewayBillData.setToAddr2(toAddr2);
                        ewayBillData.setToPlace(toPlace);
                        ewayBillData.setToPincode(toPincode);
                        ewayBillData.setToStateCode(toStateCode);
                        ewayBillData.setTotalValue(totalValue);
                        ewayBillData.setTotInvValue(totInvValue);
                        ewayBillData.setCgstValue(cgstValue);
                        ewayBillData.setSgstValue(sgstValue);
                        ewayBillData.setIgstValue(igstValue);
                        ewayBillData.setCessValue(cessValue);
                        ewayBillData.setTransporterId(transporterId);
                        ewayBillData.setTransporterName(transporterName);
                        ewayBillData.setStatus(status);
                        ewayBillData.setActualDist(actualDist);
                        ewayBillData.setNoValidDays(noValidDays);
                        ewayBillData.setValidUpto(validUpto);
                        ewayBillData.setExtendedTimes(extendedTimes);
                        ewayBillData.setRejectStatus(rejectStatus);
                        ewayBillData.setVehicleType(vehicleType);
                        ewayBillData.setActFromStateCode(actFromStateCode);
                        ewayBillData.setActToStateCode(actToStateCode);
                        ewayBillData.setTransactionType(transactionType);
                        ewayBillData.setOtherValue(otherValue);
                        ewayBillData.setCessNonAdvolValue(cessNonAdvolValue);
                        ewayBillData.setTimestamp(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Kolkata")).minusDays(1)));

                        ewayBillDetailsDao.save(ewayBillData);

                        try{
                            JSONArray itemListDetailsArray=detailsByEwbNo.getJSONArray("itemList");
                            for(int j=0;j<itemListDetailsArray.length();j++){

                                ItemListDetails itemListDetailsClass=new ItemListDetails();
                                JSONObject itemListObject=itemListDetailsArray.getJSONObject(j);

                                long itemNo=itemListObject.getLong("itemNo");
                                long productId=itemListObject.getLong("productId");
                                String productName=itemListObject.getString("productName");
                                String productDesc=itemListObject.getString("productDesc");
                                long hsnCode=itemListObject.getLong("hsnCode");
                                double quantity=itemListObject.getDouble("quantity");
                                String qtyUnit=itemListObject.getString("qtyUnit");
                                double cgstRate=itemListObject.getDouble("cgstRate");
                                double sgstRate=itemListObject.getDouble("sgstRate");
                                double igstRate=itemListObject.getDouble("igstRate");
                                double cessRate=itemListObject.getDouble("cessRate");
                                double cessNonAdvol=itemListObject.getDouble("cessNonAdvol");
                                double taxableAmount=itemListObject.getDouble("taxableAmount");

                                itemListDetailsClass.setItemNo(itemNo);
                                itemListDetailsClass.setProductId(productId);
                                itemListDetailsClass.setProductName(productName);
                                itemListDetailsClass.setProductDesc(productDesc);
                                itemListDetailsClass.setHsnCode(hsnCode);
                                itemListDetailsClass.setQuantity(quantity);
                                itemListDetailsClass.setQtyUnit(qtyUnit);
                                itemListDetailsClass.setCgstRate(cgstRate);
                                itemListDetailsClass.setSgstRate(sgstRate);
                                itemListDetailsClass.setIgstRate(igstRate);
                                itemListDetailsClass.setCessRate(cessRate);
                                itemListDetailsClass.setCessNonAdvol(cessNonAdvol);
                                itemListDetailsClass.setTaxableAmount(taxableAmount);
                                itemListDetailsClass.setEwayBillDetails(ewayBillData);

                                itemListDao.save(itemListDetailsClass);
                            }
                        }catch(Exception e){
                            log.info(e.toString());
                        }

                        try{
                            JSONArray vehicleListDetailsArray=detailsByEwbNo.getJSONArray("VehiclListDetails");
                            for(int j=0;j<vehicleListDetailsArray.length();j++){

                                VehicleListDetails vehicleListDetailsClass=new VehicleListDetails();
                                JSONObject vehicleListObject=vehicleListDetailsArray.getJSONObject(j);

                                String updMode=vehicleListObject.getString("updMode");
                                String vehicleNo=vehicleListObject.getString("vehicleNo");
                                String fromPlaceCode=vehicleListObject.getString("fromPlace");
                                long fromState=vehicleListObject.getLong("fromState");
                                long tripshtNo=vehicleListObject.getLong("tripshtNo");
                                String userGSTINTransin=vehicleListObject.getString("userGSTINTransin");
                                String enteredDate=vehicleListObject.getString("enteredDate");
                                String transMode=vehicleListObject.getString("transMode");
                                String transDocNo=vehicleListObject.getString("transDocNo");
                                String transDocDate=vehicleListObject.getString("transDocDate");
                                String groupNo=vehicleListObject.getString("groupNo");

                                vehicleListDetailsClass.setUpdMode(updMode);
                                vehicleListDetailsClass.setVehicleNo(vehicleNo);
                                vehicleListDetailsClass.setFromPlace(fromPlaceCode);
                                vehicleListDetailsClass.setFromState(fromState);
                                vehicleListDetailsClass.setTripshtNo(tripshtNo);
                                vehicleListDetailsClass.setUserGSTINTransin(userGSTINTransin);
                                vehicleListDetailsClass.setEnteredDate(enteredDate);
                                vehicleListDetailsClass.setTransMode(transMode);
                                vehicleListDetailsClass.setTransDocNo(transDocNo);
                                vehicleListDetailsClass.setTransDocDate(transDocDate);
                                vehicleListDetailsClass.setGroupNo(groupNo);
                                vehicleListDetailsClass.setEwayBillDetails(ewayBillData);

                                vehicleListDao.save(vehicleListDetailsClass);
                            }
                        }catch(Exception e){
                            log.info(e.toString());
                        }
                    }    
                }            
            }
        }  
    }
}
