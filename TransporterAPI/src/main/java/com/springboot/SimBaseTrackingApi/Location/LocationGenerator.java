package com.springboot.SimBaseTrackingApi.Location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.springboot.SimBaseTrackingApi.Authentication.AirtelAuthentication;
import com.springboot.SimBaseTrackingApi.Authentication.JioAuthentication;
import com.springboot.SimBaseTrackingApi.Authentication.VodafoneAuthentication;
import com.springboot.SimBaseTrackingApi.Entity.TrackingData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LocationGenerator {

    @Autowired
    public JioAuthentication jio;

    @Autowired
    public  VodafoneAuthentication voda;

    @Autowired
    public  AirtelAuthentication airtel;

    @Value ("${JioLocationUrl}")
    String jioLocationUrl;

    @Value ("${VodafoneLocationUrl}")
    String vodaLocationUrl;

    @Value ("${AirtelGetAllDeviceUrl}")
    String airtelGetAllDeviceUrl;

    @Value("${TraccarUrl}")
    String traccarUrl;

    @Async
    public void TrackingResponse(TrackingData data) throws URISyntaxException, IOException {

        try{

        String operatorName=data.getOperatorName();
        String mobileNumber=data.getMobileNumber();
        String latitude=null;
        String longitude=null;
        String serverTime=null;
        HttpURLConnection webConnection=null;
        if(operatorName.equals("Reliance Jio Infocomm Ltd (RJIL)")){

            URL weburl=new URL(jioLocationUrl+mobileNumber);
            webConnection = (HttpURLConnection) weburl.openConnection();
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
            latitude=locationResponse.getString("lat");
            longitude=locationResponse.getString("long");
            serverTime=locationResponse.getString("serverTime");
            }  
        }  
        else if(operatorName.equals("Vodafone Idea Ltd (formerly Vodafone India Ltd)")){

            URL weburl=new URL(vodaLocationUrl+"91"+mobileNumber);
            webConnection = (HttpURLConnection) weburl.openConnection();
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
            latitude=Double.toString(locationResponse.getDouble("latitude"));
            longitude=Double.toString(locationResponse.getDouble("longitude"));
            serverTime=locationResponse.getString("timestamp");
            } 
        }                    
        else if(operatorName.equals("Bharti Airtel Ltd")){
            
            URL weburl=new URL(airtelGetAllDeviceUrl+"91"+mobileNumber+"/location");
            webConnection = (HttpURLConnection) weburl.openConnection();
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
            latitude=Double.toString(locationResponse.getJSONObject("location").getDouble("latitude"));
            longitude=Double.toString(locationResponse.getJSONObject("location").getDouble("longitude"));
            serverTime=locationResponse.getString("retrievedAt");
            }  
        }
        if(latitude!=null && longitude!=null && serverTime!=null){
            // We can't leave a blank space inbetween a URL, so we are breaking up the timestamp and between
            // the gap of date and time we can add %20. We are sending this information to Trccar service from here.
            String date=serverTime.substring(0, 10);
            String time=serverTime.substring(11, 19);
            URL weburl=new URL(traccarUrl
                                        +"?id="+mobileNumber
                                        +"&lat="+latitude
                                        +"&lon="+longitude
                                        +"&timestamp="+date+"%20"+time
                                        +"&hdop=0"
                                        +"&altitude=0"
                                        +"&speed=0");
            HttpURLConnection traccarConnection = (HttpURLConnection) weburl.openConnection();
            traccarConnection.setRequestMethod("GET");
            traccarConnection.setDoOutput(true);
            traccarConnection.getResponseCode();
        }
    }catch(Exception e){
        log.info(e.toString());
    }
    }
}
