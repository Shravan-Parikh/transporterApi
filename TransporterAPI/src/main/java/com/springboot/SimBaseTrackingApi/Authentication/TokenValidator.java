package com.springboot.SimBaseTrackingApi.Authentication;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class TokenValidator {

    @Autowired
    public JioAuthentication jio;

    @Autowired
    public  VodafoneAuthentication voda;

    @Autowired
    public  AirtelAuthentication airtel;

    @Value("${JioValidateTokenUrl}")
    String validateJioTokenUrl;

    @Value("${VodafoneConsentStatusUrl}")
    String validateVodaTokenUrl;

    @Value("${AirtelValidateTokenUrl}")
    String validateAirtelTokenUrl;
    
    public void validator() throws URISyntaxException, IOException{
        URL jioUrl = new URL(validateJioTokenUrl);
        URL vodaUrl = new URL(validateVodaTokenUrl);
        URL airtelUrl = new URL(validateAirtelTokenUrl);
        validateJioToken(jioUrl);
        validateVodafoneToken(vodaUrl);
        validateAirtelResourceToken(airtelUrl);
        validateAirtelLocationToken(airtelUrl);
    }
    public void validateJioToken(URL jioUrl) throws URISyntaxException, IOException{
        HttpURLConnection webConnection=null;
        int statusCode=0;
        try{
            webConnection = (HttpURLConnection) jioUrl.openConnection();
            webConnection.setRequestMethod("GET");
            webConnection.setRequestProperty("x-access-token", jio.getToken());
            statusCode=webConnection.getResponseCode();
        }catch(Exception e){
            if(statusCode==401){
                jio.generateToken();
            }
        }
    }
    public void validateVodafoneToken(URL vodaUrl) throws URISyntaxException, IOException{

        HttpURLConnection webConnection=null;
        int statusCode=0;
        try{
            webConnection = (HttpURLConnection) vodaUrl.openConnection();
            webConnection.setRequestMethod("GET");
            webConnection.setRequestProperty("token", voda.getToken());
            statusCode=webConnection.getResponseCode();
        }catch(Exception e){
            if(statusCode==401){
                voda.generateToken();
            }
        }
    }


    public void validateAirtelLocationToken(URL airtelUrl) throws URISyntaxException, IOException{

        HttpURLConnection webConnection=null;
        int statusCode=0;
        try{
            webConnection = (HttpURLConnection) airtelUrl.openConnection();
            webConnection.setRequestMethod("GET");
            webConnection.setRequestProperty("access_token", airtel.getLocationToken());
            statusCode=webConnection.getResponseCode();
        }catch(Exception e){
            if(statusCode==401){
                airtel.generateLocationToken();
            }
        }
    }

    public void validateAirtelResourceToken(URL airtelUrl) throws URISyntaxException, IOException{

        HttpURLConnection webConnection=null;
        int statusCode=0;
        try{
            webConnection = (HttpURLConnection) airtelUrl.openConnection();
            webConnection.setRequestMethod("GET");
            webConnection.setRequestProperty("access_token", airtel.getResourceToken());
            statusCode=webConnection.getResponseCode();
        }catch(Exception e){
            if(statusCode==401){
                airtel.generateResourceToken();
            }
        }
    }
}
