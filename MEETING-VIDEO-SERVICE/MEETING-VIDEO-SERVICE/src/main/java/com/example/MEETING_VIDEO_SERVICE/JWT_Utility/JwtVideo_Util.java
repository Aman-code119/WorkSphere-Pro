package com.example.MEETING_VIDEO_SERVICE.JWT_Utility;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtVideo_Util {

    // 🔑 Ek strong 256-bit Secret Key secure token signing ke liye
    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 🏢 MNC Identifier (Jitsi App ID)
    private final String JITSI_APP_ID = "MNC_Corporate_Video_Network";

    public String generateSecureVideoToken(String userId, String userName, String roomName, int durationMinutes) {

        // 🧑‍💻 User details ko context mapping me daalna (Jitsi standard claims)
        Map<String, Object> userContext = new HashMap<>();
        userContext.put("id", userId);
        userContext.put("name", userName);
        userContext.put("avatar", "https://api.dicebear.com/7.x/initials/svg?seed=" + userName); // Dynamic profile pic

        Map<String, Object> context = new HashMap<>();
        context.put("user", userContext);
        context.put("features", Map.of(
                "screen-sharing", true,
                "chat", true,
                "recording", false // Security ke liye recording off rakh rahe hain
        ));

        long expirationMillis = System.currentTimeMillis() + ((long) durationMinutes * 60 * 1000);

        // 📝 JWT Token building process
        return Jwts.builder()
                .setSubject(userId)
                .setIssuer(JITSI_APP_ID)               // Kisne token banaya
                .setAudience(roomName)                  // Yeh token sirf isi room me chalega
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(expirationMillis))
                .claim("context", context)             // Custom features and user info
                .signWith(SECRET_KEY)                   // Safe digital signature
                .compact();
    }
}
