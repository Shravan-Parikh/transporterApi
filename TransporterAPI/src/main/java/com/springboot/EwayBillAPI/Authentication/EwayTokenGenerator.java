package com.springboot.EwayBillAPI.Authentication;

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
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Configuration
public class EwayTokenGenerator {

    private String authToken;

    private String sek;

    @Value("${EwayBillAccessToken}")
    String accessToken;

    @Value("${EwayBillAuthenticationUrl}")
    String ewayBillAuthenticationUrl;

    public void generateToken(String username, String password, String gstin) throws URISyntaxException, IOException{
        
        try{
        URL weburl=new URL(ewayBillAuthenticationUrl);
        String authString="Bearer "+accessToken;
        HttpURLConnection webConnection = (HttpURLConnection) weburl.openConnection();
        webConnection.setRequestMethod("POST");
        webConnection.setRequestProperty("Accept", "application/json");
        webConnection.setRequestProperty("Content-Type", "application/json");
        webConnection.setRequestProperty("Authorization", authString);
        webConnection.setRequestProperty("gstin", gstin);
        webConnection.setDoOutput(true);

        JSONObject jsonData = new JSONObject();
            jsonData.put("username", username);
            jsonData.put("password", password);

        try (OutputStream outStream = webConnection.getOutputStream()) {
                byte[] reqBody = jsonData.toString().getBytes(StandardCharsets.UTF_8);
                outStream.write(reqBody, 0, reqBody.length);
            }

            // We are reading the response using getInputStream()
        try (BufferedReader br = new BufferedReader(new InputStreamReader(webConnection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder resp = new StringBuilder();
            String respLine = null;
            while ((respLine = br.readLine()) != null) {
                resp.append(respLine.trim());
            }    
            JSONObject respJson = new JSONObject(resp.toString());  
            this.authToken=respJson.getJSONObject("Data").getString("AuthToken");
            this.sek=respJson.getJSONObject("Data").getString("Sek");
        }
    }catch(Exception e){
        log.info(e.toString());
    }
    }

    public String getAuthToken(){
        return authToken;
    }

    public String getSek(){
        return sek;
    }
}
