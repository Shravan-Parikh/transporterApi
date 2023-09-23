package com.springboot.SimBaseTrackingApi.Authentication;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Configuration
public class AirtelAuthentication {

    private String resourceToken;

    private String locationToken;

    @Value("${AirtelUsername}") 
    String username;
    
    @Value ("${AirtelPassword}") 
    String password;

    @Value ("${AirtelAuthenticationUrl}") 
    String airtelUrl;

    @Value ("${AirtelLocationUrl}")
    String airtelLocationUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl(airtelLocationUrl).build();
    }
    // 23 Hour
    @Scheduled(fixedRate = 82800000)
    public  void generateResourceToken() throws URISyntaxException, IOException{
        
        URL weburl=new URL(airtelUrl);
        HttpURLConnection webConnection = (HttpURLConnection) weburl.openConnection();
        webConnection.setRequestMethod("POST");
        webConnection.setRequestProperty("Accept", "application/json");
        webConnection.setRequestProperty("Content-Type", "application/json");
        webConnection.setDoOutput(true);


         JSONObject jsonData = new JSONObject();
            jsonData.put("client_id", this.username);
            jsonData.put("client_secret", this.password);
            jsonData.put("scope", "resource");
            try (OutputStream outStream = webConnection.getOutputStream()) {
                byte[] reqBody = jsonData.toString().getBytes(StandardCharsets.UTF_8);
                outStream.write(reqBody, 0, reqBody.length);
            }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(webConnection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder resp = new StringBuilder();
            String respLine = null;
            while ((respLine = br.readLine()) != null) {
                resp.append(respLine.trim());
            }    
            JSONObject respJson = new JSONObject(resp.toString());  
            this.resourceToken=respJson.getString("access_token");
        }
    }

    @Scheduled(fixedRate = 82800000)
    public  void generateLocationToken() throws URISyntaxException, IOException{
            
        URL weburl=new URL(airtelUrl);
        HttpURLConnection webConnection = (HttpURLConnection) weburl.openConnection();
        webConnection.setRequestMethod("POST");
        webConnection.setRequestProperty("Accept", "application/json");
        webConnection.setRequestProperty("Content-Type", "application/json");
        webConnection.setDoOutput(true);

         JSONObject jsonData = new JSONObject();
            jsonData.put("client_id", this.username);
            jsonData.put("client_secret", this.password);
            jsonData.put("scope", "location");
            try (OutputStream outStream = webConnection.getOutputStream()) {
                byte[] reqBody = jsonData.toString().getBytes(StandardCharsets.UTF_8);
                outStream.write(reqBody, 0, reqBody.length);
            }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(webConnection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder resp = new StringBuilder();
            String respLine = null;
            while ((respLine = br.readLine()) != null) {
                resp.append(respLine.trim());
            }    
            JSONObject respJson = new JSONObject(resp.toString());  
            this.locationToken=respJson.getString("access_token");
        }
    }

    
    public String getResourceToken(){
        return resourceToken;
    }

    public String getLocationToken(){
        return locationToken;
    }
}
