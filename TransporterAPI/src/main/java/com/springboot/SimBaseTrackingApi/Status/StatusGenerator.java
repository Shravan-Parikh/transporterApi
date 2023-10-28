package com.springboot.SimBaseTrackingApi.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.springboot.SimBaseTrackingApi.AirtelStartTrackingData;
import com.springboot.SimBaseTrackingApi.Authentication.AirtelAuthentication;
import com.springboot.SimBaseTrackingApi.Authentication.JioAuthentication;
import com.springboot.SimBaseTrackingApi.Authentication.VodafoneAuthentication;
import com.springboot.SimBaseTrackingApi.Dao.TrackingDao;
import com.springboot.SimBaseTrackingApi.Entity.TrackingData;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class StatusGenerator {

    @Autowired
    private TrackingDao trackingDao;

    @Autowired
    public JioAuthentication jio;

    @Autowired
    public  VodafoneAuthentication voda;

    @Autowired
    public  AirtelAuthentication airtel;

    @Value ("${JioLocationUrl}")
    String jioLocationUrl;

    @Value("${StartJioUrl}")
    String startJioUrl;

    @Value("${VodafoneConsentStatusUrl}")
    String vodafoneConsentStatusUrl;

    @Value ("${AirtelGetAllDeviceUrl}")
    String airtelGetAllDeviceUrl;

    @Async
    public void ConsentStatus(TrackingData data) throws IOException, URISyntaxException{

        try{
            String pseudoStatus="";
            boolean tracking=false;

            String operatorName=data.getOperatorName();
            String mobileNumber=data.getMobileNumber();
            if(operatorName.equals("Reliance Jio Infocomm Ltd (RJIL)")){

                URL weburl=new URL(jioLocationUrl+mobileNumber);
                HttpURLConnection webConnection = (HttpURLConnection) weburl.openConnection();
                webConnection.setRequestMethod("GET");
                webConnection.setRequestProperty("Accept", "application/json");
                webConnection.setRequestProperty("Content-Type", "application/json");
                webConnection.setRequestProperty("x-access-token", jio.getToken());
                webConnection.setDoOutput(true);
                webConnection.connect();

                try (BufferedReader br = new BufferedReader(new InputStreamReader(webConnection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder resp = new StringBuilder();
                    String respLine = null;
                    while ((respLine = br.readLine()) != null) {
                        resp.append(respLine.trim());
                    }            
                pseudoStatus=new JSONObject(resp.toString()).getString("consentStatus").toUpperCase();
                }  
            }
            else if(operatorName.equals("Vodafone Idea Ltd (formerly Vodafone India Ltd)")){

                URL weburl=new URL(vodafoneConsentStatusUrl+"?search=91"+mobileNumber);
                HttpURLConnection webConnection = (HttpURLConnection) weburl.openConnection();
                webConnection.setRequestMethod("GET");
                webConnection.setRequestProperty("Accept", "application/json");
                webConnection.setRequestProperty("Content-Type", "application/json");
                webConnection.setRequestProperty("token", voda.getToken());
                webConnection.setDoOutput(true);
                webConnection.connect();

                try (BufferedReader br = new BufferedReader(new InputStreamReader(webConnection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder resp = new StringBuilder();
                    String respLine = null;
                    while ((respLine = br.readLine()) != null) {
                        resp.append(respLine.trim());
                    }            
                pseudoStatus=new JSONObject(resp.toString())
                                                    .getJSONArray("data")
                                                    .getJSONObject(0)
                                                    .getString("consentStatus").toUpperCase();
                }  
            }
            else if(operatorName.equals("Bharti Airtel Ltd")){

                URL weburl=new URL(airtelGetAllDeviceUrl+"91"+mobileNumber);
                HttpURLConnection webConnection = (HttpURLConnection) weburl.openConnection();
                webConnection.setRequestMethod("GET");
                webConnection.setRequestProperty("Accept", "application/json");
                webConnection.setRequestProperty("Content-Type", "application/json");
                webConnection.setRequestProperty("access_token", airtel.getResourceToken());
                webConnection.setDoOutput(true);
                webConnection.connect();

                try (BufferedReader br = new BufferedReader(new InputStreamReader(webConnection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder resp = new StringBuilder();
                    String respLine = null;
                    while ((respLine = br.readLine()) != null) {
                        resp.append(respLine.trim());
                    }            
                pseudoStatus=new JSONObject(resp.toString()).getString("consent").toUpperCase();
                tracking=new JSONObject(resp.toString()).getBoolean("tracking");
                }  
            }

            if(pseudoStatus.equals("ACTIVE") && data.getStatus().equals("PENDING")){
                data.setStatus("APPROVED");
            }
            else if(pseudoStatus.equals("CONSENT_APPROVED")){

                String s = StartTracking(mobileNumber,operatorName);
                if(s.equals("APPROVED")){
                    data.setStatus("APPROVED");
                }
            }
            else if(pseudoStatus.equals("ALLOWED") && data.getStatus().equals("PENDING")){
                if(tracking==false){
                    String s= StartTracking(mobileNumber,operatorName);
                    if(s.equals("APPROVED")){
                        data.setStatus("APPROVED");
                    }
                }
                else{
                    data.setStatus("APPROVED");
                }
            }
            else if(pseudoStatus.equals("REJECTED")
            || pseudoStatus.equals("REVOKED") ||pseudoStatus.equals("EXPIRED")
            || pseudoStatus.equals("DEREGISTERED") || pseudoStatus.equals("CONSENT_DENIED")){
                data.setStatus("REJECTED");
            }
            trackingDao.save(data);
        }catch(Exception e){
            log.info(e.toString());
        }
    }

    public String StartTracking(String mobileNumber, String operatorName) throws IOException, URISyntaxException{

        String status="APPROVED";
        HttpURLConnection webConnection=null;
        JSONObject jsonData = new JSONObject();

        try{
            if(operatorName.equals("Reliance Jio Infocomm Ltd (RJIL)")){
                URL weburl=new URL(startJioUrl);
                webConnection = (HttpURLConnection) weburl.openConnection();
                webConnection.setRequestMethod("PUT");
                webConnection.setRequestProperty("Accept", "application/json");
                webConnection.setRequestProperty("Content-Type", "application/json");
                webConnection.setRequestProperty("x-access-token", jio.getToken());
                webConnection.setDoOutput(true);

                jsonData.put("mobileNumber", mobileNumber);
                try(OutputStream outStream = webConnection.getOutputStream()){
                    byte[] reqBody = jsonData.toString().getBytes(StandardCharsets.UTF_8);
                    outStream.write(reqBody, 0, reqBody.length);
                }   
                webConnection.getResponseCode(); 
            }  
            else if(operatorName.equals("Bharti Airtel Ltd")){

                AirtelStartTrackingData data=new AirtelStartTrackingData();
                data.setIsTrackingEnabled("true");
                WebClient client=WebClient.create(airtelGetAllDeviceUrl+"91"+mobileNumber);
                
               Mono<Object> resp=client.patch()
                        .header("access_token", airtel.getResourceToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(data), AirtelStartTrackingData.class)
                        .exchangeToMono(response -> {
                            if (response.statusCode().is4xxClientError()) {
                                return response.bodyToMono(String.class);
                            }
                            else {
                                return response.bodyToMono(Void.class);
                            }
                            }
                        );
                    if(resp.hasElement().block().booleanValue()==true){
                        status="EXCEPTION";
                    }                       
            } 
        }catch(Exception e){
            status="EXCEPTION";
        }
        return status;
    }
}
