package com.springboot.SimBaseTrackingApi.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.springboot.SimBaseTrackingApi.AirtelStartTrackingData;
import com.springboot.SimBaseTrackingApi.LocationResponse;
import com.springboot.SimBaseTrackingApi.Authentication.AirtelAuthentication;
import com.springboot.SimBaseTrackingApi.Authentication.JioAuthentication;
import com.springboot.SimBaseTrackingApi.Authentication.VodafoneAuthentication;
import com.springboot.SimBaseTrackingApi.Entity.Entity;
import com.springboot.SimBaseTrackingApi.Status.ConsentStatus;

import reactor.core.publisher.Mono;


@Service
public class TrackingService {

        @Autowired
        public JioAuthentication jio;

        @Autowired
        public  VodafoneAuthentication voda;

        @Autowired
        public  AirtelAuthentication airtel;

        @Value ("${JioConsentUrl}") 
        String jioConsentUrl;

        @Value("${StartJioUrl}")
        String startJioUrl;

        @Value ("${JioLocationUrl}")
        String jioLocationUrl;


        @Value ("${VodafoneConsentUrl}") 
        String vodaConsentUrl;

        @Value ("${VodafoneLocationUrl}")
        String vodaLocationUrl;

        @Value("${VodafoneConsentStatusUrl}")
        String vodafoneConsentStatusUrl;


        @Value ("${AirtelConsentUrl}") 
        String airtelConsentUrl;


        @Value ("${AirtelLocationUrl}")
        String airtelLocationUrl;


        @Value ("${OperatorUrl}")
        String operatorUrl;

        @Value ("${OperatorKey}")
        String accessKey;

    public  String getConsentResponse(Entity entity) throws IOException{

        String status="Invalid Number";
        try{
            String mobileNumber=entity.getMobileNumber();
            int consentDurationInDays=entity.getConsentDurationInDays();
            String customerName=entity.getCustomerName();
            String operatorName=entity.getOperatorName();
            JSONObject jsonData = new JSONObject();
            HttpURLConnection webConnection=null;

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
                if(customerName!=null){
                    jsonData.put("customerName", customerName);
                }
            }
            else if(operatorName.equals("Vodafone Idea Ltd (formerly Vodafone India Ltd)")){    

                URL weburl=new URL(vodaConsentUrl);
                webConnection = (HttpURLConnection) weburl.openConnection();
                webConnection.setRequestMethod("POST");
                webConnection.setRequestProperty("Accept", "application/json");
                webConnection.setRequestProperty("Content-Type", "application/json");
                webConnection.setRequestProperty("token", voda.getToken());
                webConnection.setDoOutput(true);

                JSONObject data = new JSONObject();
                    data.put("msisdn", "91"+mobileNumber);
                    if(customerName!=null){
                        data.put("firstName", customerName);
                    }
                JSONArray jsonArray=new JSONArray();
                jsonArray.put(data);
                jsonData.put("entityImportList", jsonArray);
            }
            else if(operatorName.equals("Bharti Airtel Ltd")){    

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
                try (OutputStream outStream = webConnection.getOutputStream()){
                    byte[] reqBody = jsonData.toString().getBytes(StandardCharsets.UTF_8);
                    outStream.write(reqBody, 0, reqBody.length);
                }
                try (BufferedReader br = new BufferedReader(new InputStreamReader(webConnection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder resp = new StringBuilder();
                    String respLine = null;
                    while ((respLine = br.readLine()) != null) {
                        resp.append(respLine.trim());
                    }            
                    JSONObject response= new JSONObject(resp.toString());
                    status=response.toString(4);
                }
            }
        }catch(Exception e){
            status="Internal Server Error";
        }
        return status;         
    }   

    public String getStartTrackingResponse(String mobileNumber) throws IOException, URISyntaxException{

        String status="Invalid Number";
        try{
            String operatorName=getOperaterName(mobileNumber);
            HttpURLConnection webConnection=null;
            JSONObject jsonData = new JSONObject();

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
                try (BufferedReader br = new BufferedReader(new InputStreamReader(webConnection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder resp = new StringBuilder();
                    String respLine = null;
                    while ((respLine = br.readLine()) != null) {
                        resp.append(respLine.trim());
                    }            
                    JSONObject response= new JSONObject(resp.toString());
                    status=response.toString(4);
                }     
            }  
            else if(operatorName.equals("Bharti Airtel Ltd")){
  
                AirtelStartTrackingData data=new AirtelStartTrackingData();
                data.setIsTrackingEnabled("true");
                WebClient client=WebClient.create(airtelLocationUrl+"91"+mobileNumber);

                Mono<Object> resp=client.patch()
                                        .header("access_token", airtel.getResourceToken())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body(Mono.just(data), AirtelStartTrackingData.class)
                                        .exchangeToMono(response -> {
                                            if (response.statusCode().is4xxClientError()) {
                                                return response.bodyToMono(String.class);
                                            }
                                            else{
                                                return response.bodyToMono(Void.class);
                                            }
                                        });

                if(resp.hasElement().block().booleanValue()){
                     status=resp.block().toString();
                }
                else{
                    status="Tracking Activated";
                }
            } 
        }catch(Exception e){
            status="Internal Server Error";
        }
        return status;
    }

    public LocationResponse getTrackingResponse(String mobileNumber) throws IOException, InterruptedException{

        LocationResponse location=new LocationResponse();
        try{
            String operatorName=getOperaterName(mobileNumber);

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
                JSONObject locationResponse= new JSONObject(resp.toString()).getJSONObject("locationInfo");
                location.setLatitude(locationResponse.getString("lat"));
                location.setLongitude(locationResponse.getString("long"));
                location.setServerTime(locationResponse.getString("serverTime"));
                }  
            }  
            else if(operatorName.equals("Vodafone Idea Ltd (formerly Vodafone India Ltd)")){

                URL weburl=new URL(vodaLocationUrl+"91"+mobileNumber);
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
                JSONObject locationResponse= new JSONObject(resp.toString())
                                                .getJSONArray("terminalLocation")
                                                .getJSONObject(0)
                                                .getJSONObject("currentLocation");
                location.setLatitude(Double.toString(locationResponse.getDouble("latitude")));
                location.setLongitude(Double.toString(locationResponse.getDouble("longtitude")));
                location.setServerTime(locationResponse.getString("timestamp"));
                } 
            } 
            else if(operatorName.equals("Bharti Airtel Ltd")){

                URL weburl=new URL(airtelLocationUrl+"91"+mobileNumber+"/location");
                HttpURLConnection webConnection = (HttpURLConnection) weburl.openConnection();
                webConnection.setRequestMethod("GET");
                webConnection.setRequestProperty("Accept", "application/json");
                webConnection.setRequestProperty("Content-Type", "application/json");
                webConnection.setRequestProperty("access_token", airtel.getLocationToken());
                webConnection.setDoOutput(true);
                webConnection.connect();

                try (BufferedReader br = new BufferedReader(new InputStreamReader(webConnection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder resp = new StringBuilder();
                    String respLine = null;
                    while ((respLine = br.readLine()) != null) {
                        resp.append(respLine.trim());
                    }            
                JSONObject locationResponse= new JSONObject(resp.toString());
                location.setLatitude(Double.toString(locationResponse.getJSONObject("location").getDouble("latitude")));
                location.setLongitude(Double.toString(locationResponse.getJSONObject("location").getDouble("longitude")));
                location.setServerTime(locationResponse.getString("retrievedAt"));
                }  
            }
        }catch(Exception e){
            location.setError("Internal Server Error");
        }
        return location; 
    }

    public ConsentStatus getConsentStatus(String mobileNumber) throws IOException{

        ConsentStatus consent=new ConsentStatus();
        try{
            String operatorName=getOperaterName(mobileNumber);

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
                consent.setConsentStatus(new JSONObject(resp.toString()).getString("consentStatus"));
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
                consent.setConsentStatus(new JSONObject(resp.toString())
                                                    .getJSONArray("data")
                                                    .getJSONObject(0)
                                                    .getString("consentStatus"));
                }  
            }
            else if(operatorName.equals("Bharti Airtel Ltd")){

                URL weburl=new URL(airtelLocationUrl+"91"+mobileNumber+"/consent");
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
                consent.setConsentStatus(new JSONObject(resp.toString()).getString("consent"));
                }  
            }
        }catch (Exception e){
            consent.setError("Internal Server Error");;
        }
        return consent;  
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
