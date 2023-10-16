package com.springboot.SimBaseTrackingApi.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.springboot.SimBaseTrackingApi.Authentication.AirtelAuthentication;
import com.springboot.SimBaseTrackingApi.Authentication.JioAuthentication;
import com.springboot.SimBaseTrackingApi.Authentication.TokenValidator;
import com.springboot.SimBaseTrackingApi.Authentication.VodafoneAuthentication;
import com.springboot.SimBaseTrackingApi.Dao.TrackingDao;
import com.springboot.SimBaseTrackingApi.Entity.TrackingData;
import com.springboot.SimBaseTrackingApi.Entity.TrackingEntity;
import com.springboot.SimBaseTrackingApi.Status.ConsentStatus;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class TrackingService {

        @Autowired
        public JioAuthentication jio;

        @Autowired
        public  VodafoneAuthentication voda;

        @Autowired
        public  AirtelAuthentication airtel;

        @Autowired
        private TrackingDao trackingDao;

        @Autowired
        private TokenValidator tokenValidator;


        @Value ("${JioConsentUrl}") 
        String jioConsentUrl;

        @Value("${JioGetAllDeviceUrl}")
        String jioGetAllDeviceUrl;

        @Value("${JioLocationUrl}")
        String jioLocationUrl;

        @Value("${VodafoneConsentStatusUrl}")
        String vodafoneConsentStatusUrl;

        @Value ("${VodafoneConsentUrl}") 
        String vodaConsentUrl;

        @Value ("${AirtelConsentUrl}") 
        String airtelConsentUrl;

        @Value("${AirtelGetAllDeviceUrl}")
        String airtelGetAllDeviceUrl;

        @Value ("${OperatorUrl}")
        String operatorUrl;

        @Value ("${OperatorKey}")
        String accessKey;

    public  ConsentStatus getConsentResponse(TrackingEntity entity) throws IOException, URISyntaxException{

        String mobileNumber=entity.getMobileNumber();
        int consentDurationInDays=entity.getConsentDurationInDays();
        String driverName=entity.getDriverName();
        String operatorName=entity.getOperatorName();
        JSONObject jsonData = new JSONObject();
        HttpURLConnection webConnection=null;
        ConsentStatus status=new ConsentStatus();
        try{
            if(operatorName!=null){
                operatorName=operatorName.toUpperCase();
                if(operatorName.equals("JIO")){
                    operatorName="Reliance Jio Infocomm Ltd (RJIL)";
                }
                else if(operatorName.equals("VODAFONE")){
                    operatorName="Vodafone Idea Ltd (formerly Vodafone India Ltd)";
                }
                else if(operatorName.equals("AIRTEL")){
                    operatorName="Bharti Airtel Ltd";
                }
            }
            else{
                operatorName=getOperaterName(mobileNumber);
            }

            if(operatorName.equals("Reliance Jio Infocomm Ltd (RJIL)")){

                // Validating the token before proceed
                tokenValidator.validateJioToken(new URL(jioGetAllDeviceUrl));
                URL weburl=new URL(jioConsentUrl);
                webConnection = (HttpURLConnection) weburl.openConnection();
                webConnection.setRequestMethod("POST");
                webConnection.setRequestProperty("Accept", "application/json");
                webConnection.setRequestProperty("Content-Type", "application/json");
                webConnection.setRequestProperty("x-access-token", jio.getToken());
                webConnection.setDoOutput(true);

                jsonData.put("mobileNumber", mobileNumber);
                if(consentDurationInDays!=0){
                    jsonData.put("consentDurationInDays", consentDurationInDays);    
                }
            }
            else if(operatorName.equals("Vodafone Idea Ltd (formerly Vodafone India Ltd)")){    

                tokenValidator.validateVodafoneToken(new URL(vodafoneConsentStatusUrl));
                URL weburl=new URL(vodaConsentUrl);
                webConnection = (HttpURLConnection) weburl.openConnection();
                webConnection.setRequestMethod("POST");
                webConnection.setRequestProperty("Accept", "application/json");
                webConnection.setRequestProperty("Content-Type", "application/json");
                webConnection.setRequestProperty("token", voda.getToken());
                webConnection.setDoOutput(true);

                JSONObject data = new JSONObject();
                data.put("msisdn", "91"+mobileNumber);
                
                JSONArray jsonArray=new JSONArray();
                jsonArray.put(data);
                jsonData.put("entityImportList", jsonArray);
            }
            else if(operatorName.equals("Bharti Airtel Ltd")){    

                tokenValidator.validateAirtelResourceToken(new URL(airtelGetAllDeviceUrl));
                URL weburl=new URL(airtelConsentUrl);
                webConnection = (HttpURLConnection) weburl.openConnection();
                webConnection.setRequestMethod("POST");
                webConnection.setRequestProperty("Accept", "application/json");
                webConnection.setRequestProperty("Content-Type", "application/json");
                webConnection.setRequestProperty("access_token", airtel.getResourceToken());
                webConnection.setDoOutput(true);

                jsonData.put("msisdn", "91"+mobileNumber);
            }
            if(webConnection!=null){

                ConsentStatus consentStatus=getConsentStatus(mobileNumber);
                TrackingData data=new TrackingData();
                if(consentStatus.getStatus().equals("REJECTED") 
                ||consentStatus.getError().equals("Device not found")
                || consentStatus.getStatus().equals("PENDING")){

                    if(consentStatus.getStatus().equals("REJECTED")
                    || consentStatus.getStatus().equals("PENDING")){
                        data.setTrackingId(trackingDao.findByMobileNumber(mobileNumber).get(0).getTrackingId());
                    } 
                    try (OutputStream outStream = webConnection.getOutputStream()){
                        byte[] reqBody = jsonData.toString().getBytes(StandardCharsets.UTF_8);
                        outStream.write(reqBody, 0, reqBody.length);
                    } 
                    int statusCode=webConnection.getResponseCode();
                    if(statusCode==200 || statusCode==202){
                        String msisdn="SUCCESS";
                        // Vodafone gives response code 200 for a success or failure, so we are checking the response then
                        // procceding accordingly and Airtel gives response code 202, and Jio 200, for success, in this case.
                        if(operatorName.equals("Vodafone Idea Ltd (formerly Vodafone India Ltd)")){
                            try (BufferedReader br = new BufferedReader(
                                new InputStreamReader(webConnection.getInputStream(), StandardCharsets.UTF_8))) {
                                StringBuilder resp = new StringBuilder();
                                String respLine = null;
                                while ((respLine = br.readLine()) != null) {
                                    resp.append(respLine.trim());
                                }  
                                JSONObject respJson = new JSONObject(resp.toString());
                                try{
                                    msisdn=respJson
                                            .getJSONArray("failureList")
                                            .getJSONObject(0)
                                            .getString("msisdn");
                                }catch(Exception e){
                                    msisdn="SUCCESS";
                                }
                            }
                        }
                        if(msisdn.equals("SUCCESS")){
                        data.setMobileNumber(mobileNumber);
                        data.setDriverName(driverName);
                        data.setOperatorName(operatorName);
                        data.setStatus("PENDING");
                        trackingDao.save(data);
                        status.setStatus("Consent Send to driver");
                        }
                        else{
                            status.setStatus("Sending Consent disallowed by "+operatorName);
                        }
                    }
                    else if(statusCode==400){
                        status.setStatus("Sending Consent disallowed by "+operatorName);
                    }
                    else{
                        status.setError("Internal Server error");
                        log.info(statusCode+" Error code while registering Device");
                    }
                }
                else{
                    status.setStatus("Device already registered");
                }
            }
            else{
                status.setStatus("Invalid/Insufficient Information");
            }
        }catch(Exception e){
            status.setError("Internal Server error");
            log.info(e.toString());
        }
        return status;         
    }   

    public ConsentStatus getConsentStatus(String mobileNumber) throws IOException{

        ConsentStatus consent=new ConsentStatus();
        try{
            String status=trackingDao.findByMobileNumber(mobileNumber).get(0).getStatus();
            consent.setStatus(status);
        }catch (Exception e){
            consent.setError("Device not found");;
        }
        return consent;  
    }

    public ConsentStatus deRegister(String mobileNumber){

        ConsentStatus deleteStatus=new ConsentStatus();
        try{
            TrackingData userDetails=trackingDao.findByMobileNumber(mobileNumber).get(0);
            String operatorName=userDetails.getOperatorName();
            HttpURLConnection webConnection=null;
            if(operatorName.equals("Reliance Jio Infocomm Ltd (RJIL)")){

                tokenValidator.validateJioToken(new URL(jioGetAllDeviceUrl));
                URL url=new URL(jioLocationUrl+mobileNumber);
                webConnection = (HttpURLConnection) url.openConnection();
                webConnection.setRequestMethod("DELETE");
                webConnection.setRequestProperty("x-access-token", jio.getToken());
                webConnection.setDoOutput(true);   
            }
            else if(operatorName.equals("Vodafone Idea Ltd (formerly Vodafone India Ltd)")){

                // We need the id of a number to delete it, if it is a Vodafone number. 
                // The id generates at the time of sending consent
                tokenValidator.validateVodafoneToken(new URL(vodafoneConsentStatusUrl));
                long id;
                URL weburl=new URL(vodafoneConsentStatusUrl+"?search=91"+mobileNumber);
                webConnection = (HttpURLConnection) weburl.openConnection();
                webConnection.setRequestMethod("GET");
                webConnection.setRequestProperty("token", voda.getToken());
                webConnection.setDoOutput(true);
                webConnection.connect();

                try (BufferedReader br = new BufferedReader(new InputStreamReader(webConnection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder resp = new StringBuilder();
                    String respLine = null;
                    while ((respLine = br.readLine()) != null) {
                        resp.append(respLine.trim());
                    }            
                 id=new JSONObject(resp.toString())
                                        .getJSONArray("data")
                                        .getJSONObject(0)
                                        .getLong("id");
                }  
                URL deleteUrl=new URL(vodafoneConsentStatusUrl+"/"+id+"?permanent=true");
                webConnection = (HttpURLConnection) deleteUrl.openConnection();
                webConnection.setRequestMethod("DELETE");
                webConnection.setRequestProperty("token", voda.getToken());
                webConnection.setDoOutput(true);
            }
            else if(operatorName.equals("Bharti Airtel Ltd")){

                tokenValidator.validateAirtelResourceToken(new URL(airtelGetAllDeviceUrl));
                URL url=new URL(airtelGetAllDeviceUrl+"91"+mobileNumber);
                webConnection = (HttpURLConnection) url.openConnection();
                webConnection.setRequestMethod("DELETE");
                webConnection.setRequestProperty("access_token", airtel.getResourceToken());
                webConnection.setDoOutput(true);
            }
            int statusCode=webConnection.getResponseCode();
            if(statusCode==204 || statusCode==200){
                userDetails.setStatus("REJECTED");
                trackingDao.save(userDetails);
                deleteStatus.setStatus("Device Deregistered");
            }
            else if(statusCode==404 || statusCode==400){
                userDetails.setStatus("REJECTED");
                trackingDao.save(userDetails);
                deleteStatus.setStatus("Not a registered number");
            }
            else{
                deleteStatus.setError("Failed");
                log.info(statusCode+" Error code while deregistering Device");
            }
        }catch(Exception e){
            deleteStatus.setError("Internal Server error");
            log.info(e.toString());
        }
        return deleteStatus;
    }

    public String getOperaterName(String mobileNumber) throws IOException{

            URL weburl=new URL(operatorUrl
                                +"?access_key="+accessKey
                                +"&number=" + mobileNumber
                                +"&country_code=IN"
                                +"&format=application/json");
            HttpURLConnection webConnection = (HttpURLConnection) weburl.openConnection();
            webConnection.setRequestMethod("GET");
            webConnection.setDoOutput(true);
            webConnection.connect();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(webConnection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder resp = new StringBuilder();
                String respLine = null;
                while ((respLine = br.readLine()) != null) {
                    resp.append(respLine.trim());
                }  
                JSONObject respJson = new JSONObject(resp.toString());  
                String operatorName =respJson.getString("carrier"); 
                return operatorName;        
            } 
    }
}
