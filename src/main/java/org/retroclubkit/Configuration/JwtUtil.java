//package org.retroclubkit.Configuration;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.util.Date;
//
//@Component
//public class JwtUtil {
//
//    private static final String SECRET_KEY = "your_secret_key"; // Избери си силна тайна парола
//    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 часа валидност
//
//    private SecretKey getSigningKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//
//    // Генериране на JWT токен
//    public String generateToken(String username) {
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
//                .compact();
//    }
//
//    // Извличане на потребителското име от токена
//    public String extractUsername(String token) {
//        return getClaims(token).getSubject();
//    }
//
//    // Проверка дали токенът е валиден
//    public boolean validateToken(String token, String username) {
//        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
//    }
//
//    // Проверка за изтекъл токен
//    private boolean isTokenExpired(String token) {
//        return getClaims(token).getExpiration().before(new Date());
//    }
//
//    // Разкодиране на токена и получаване на данните от него
//    private Claims getClaims(String token) {
//        return Jwts.parser()
//                .verifyWith(getSigningKey()) // Новият начин за валидация в jjwt 0.12.x
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }
//}
