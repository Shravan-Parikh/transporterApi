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
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class JioAuthentication {

    private String token;

    @Value("${JioUsername}") 
    String username;
    
    @Value ("${JioPassword}") 
    String password;

    @Value ("${JioAuthenticationUrl}") 
    String jioUrl;

    // 23 Hour
    @Scheduled(fixedRate = 82800000)
    public  void generateToken() throws URISyntaxException, IOException{
        
        //Generating the token and initializing the "token" variable with that value
        URL weburl=new URL(jioUrl);
        HttpURLConnection webConnection = (HttpURLConnection) weburl.openConnection();
        webConnection.setRequestMethod("POST");
        webConnection.setRequestProperty("Accept", "application/json");
        webConnection.setRequestProperty("Content-Type", "application/json");
        webConnection.setDoOutput(true);

         JSONObject jsonData = new JSONObject();
            jsonData.put("username", this.username);
            jsonData.put("password", this.password);
            // We are writing the JSON body to outputstream
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
            this.token=respJson.getString("token");
        }
    }
    public String getToken(){
        return token;
    }
}
