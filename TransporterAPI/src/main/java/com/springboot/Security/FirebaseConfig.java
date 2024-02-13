package com.springboot.Security;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.credentials.file}")
    private String credentialsFile;

    @Value("${firebase.app.name}")
    private String appName;

    public static void main(String[] args) {
        SpringApplication.run(FirebaseConfig.class, args);
    }

    public FirebaseConfig(@Value("${FIREBASE_URL}") String firebaseUrl) throws IOException {

        //getting url of json file
        URL url = new URL(firebaseUrl);

        //getting json file as a stream
        InputStream refreshToken = url.openStream();

        //authentication with firebase
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(refreshToken))
                .setDatabaseUrl("https://shipperwebapp-default-rtdb.asia-southeast1.firebasedatabase.app")
                .build();

    }

    @PostConstruct
    private void initializeFirebaseApp() {
        try (InputStream serviceAccount = FirebaseConfig.class.getClassLoader().getResourceAsStream(credentialsFile)) {
            assert serviceAccount != null;
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options, appName); // Provide a unique app name
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
