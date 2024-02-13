package com.springboot.Security;


import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class FirebaseAuthenticationFilter extends GenericFilterBean {

    // Use a constant for the Firebase app name
    @Value("${firebase.app.name}")
    private String FIREBASE_APP_NAME;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        // Extract Firebase token from Authorization header
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            try {
                // Verify and decode Firebase token
                FirebaseToken firebaseToken = FirebaseAuth.getInstance(FirebaseApp.getInstance(FIREBASE_APP_NAME))
                        .verifyIdToken(token.substring(7));

                // Create Spring Security Authentication object
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        firebaseToken.getEmail(), null, null);

                // Set Authentication object in SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // Handle authentication failure
                logger.error("Firebase authentication failed: " + e.getMessage());
            }
        }

        // Continue the filter chain
        chain.doFilter(req, res);
    }
}