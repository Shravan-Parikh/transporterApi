package com.springboot.SimBaseTrackingApi.Authentication;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TokenValidator {

    @Autowired
    public JioAuthentication jio;

    @Autowired
    public  VodafoneAuthentication voda;

    @Autowired
    public  AirtelAuthentication airtel;

    @Value("${JioGetAllDeviceUrl}")
    String validateJioTokenUrl;

    @Value("${VodafoneConsentStatusUrl}")
    String validateVodaTokenUrl;

    @Value("${AirtelGetAllDeviceUrl}")
    String validateAirtelTokenUrl;
    
    public void validator(String check) throws URISyntaxException, IOException{
        URL jioUrl = new URL(validateJioTokenUrl);
        URL vodaUrl = new URL(validateVodaTokenUrl);
        URL airtelLocationUrl = new URL(validateAirtelTokenUrl+123+"/location");
        URL airtelResourceUrl = new URL(validateAirtelTokenUrl);
        validateJioToken(jioUrl);
        validateVodafoneToken(vodaUrl);
        if(check.equals("validateLocation")){
            validateAirtelLocationToken(airtelLocationUrl);
        }
        else if(check.equals("validateResource")){
            validateAirtelResourceToken(airtelResourceUrl);
        }
    }
    public void validateJioToken(URL jioUrl) throws URISyntaxException, IOException{
        try{
            HttpURLConnection webConnection=null;
            int statusCode=0;
            webConnection = (HttpURLConnection) jioUrl.openConnection();
            webConnection.setRequestMethod("GET");
            webConnection.setRequestProperty("x-access-token", jio.getToken());
            statusCode=webConnection.getResponseCode();
            if(statusCode==401){
                jio.generateToken();
            }            
        }catch(Exception e){
            log.info(e.toString());
        }
    }
    public void validateVodafoneToken(URL vodaUrl) throws URISyntaxException, IOException{

        try{
            HttpURLConnection webConnection=null;
            int statusCode=0;
            webConnection = (HttpURLConnection) vodaUrl.openConnection();
            webConnection.setRequestMethod("GET");
            webConnection.setRequestProperty("token", voda.getToken());
            statusCode=webConnection.getResponseCode();
            if(statusCode==500){
                    voda.generateToken();
            }
        }catch(Exception e){
            log.info(e.toString());
        }
    }

    public void validateAirtelLocationToken(URL airtelLocationUrl) throws URISyntaxException, IOException{

        try{
            HttpURLConnection webConnection=null;
            int statusCode=0;
            webConnection = (HttpURLConnection) airtelLocationUrl.openConnection();
            webConnection.setRequestMethod("GET");
            webConnection.setRequestProperty("access_token", airtel.getLocationToken());
            statusCode=webConnection.getResponseCode();
            if(statusCode==401){
                airtel.generateLocationToken();
            }
        }catch(Exception e){
            log.info(e.toString());
        }
    }

    public void validateAirtelResourceToken(URL airtelResourceUrl) throws URISyntaxException, IOException{

        try{
            HttpURLConnection webConnection=null;
            int statusCode=0;
            webConnection = (HttpURLConnection) airtelResourceUrl.openConnection();
            webConnection.setRequestMethod("GET");
            webConnection.setRequestProperty("access_token", airtel.getResourceToken());
            statusCode=webConnection.getResponseCode();
            if(statusCode==401){
                    airtel.generateResourceToken();
            }
        }catch(Exception e){
            log.info(e.toString());
        }
    }
}
