package com.springboot.SimBaseTrackingApi.Authentication;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class VodafoneAuthentication {

    private String token;

    @Value("${VodafoneUsername}") 
    String username;
    
    @Value ("${VodafonePassword}") 
    String password;

    @Value ("${VodafoneAuthenticationUrl}") 
    String vodaUrl;


    // 1 Hour
    @Scheduled(fixedRate = 3600000)
    public  void generateToken() throws IOException{      

        URL weburl=new URL(vodaUrl);
        HttpURLConnection webConnection = (HttpURLConnection) weburl.openConnection();
        webConnection.setRequestMethod("GET");
        webConnection.setRequestProperty("Accept", "application/json");
        webConnection.setRequestProperty("Content-Type", "application/json");
        String auth = username + ":" + password;
        byte[] authBody = Base64.getEncoder().encode(auth.getBytes());
        String authHeaderValue = "Basic " + new String(authBody);
        webConnection.setRequestProperty("Authorization",authHeaderValue);
        webConnection.setDoOutput(true);
        webConnection.connect();
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(webConnection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder resp = new StringBuilder();
            String respLine = null;
            while ((respLine = br.readLine()) != null) {
                resp.append(respLine.trim());
            }    
            JSONObject respJson = new JSONObject(resp.toString());  
            this.token=respJson.getString("token");
        }
    }

    public String getToken(){
        return token;
    }
}
