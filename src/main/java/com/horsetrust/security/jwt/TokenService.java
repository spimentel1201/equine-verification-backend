package com.horsetrust.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.horsetrust.models.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.issuer:horsetrust}")
    private String issuer;

    @Value("${app.jwt.expiration-minutes:120}")
    private long accessMinutes;

    @Value("${app.jwt.refresh-minutes:10080}") // 7 días
    private long refreshMinutes;

    public String generarAccessToken(User user) {
        return generarToken(user, accessMinutes, "ACCESS");
    }

    public String generarRefreshToken(User user) {
        return generarToken(user, refreshMinutes, "REFRESH");
    }

    private String generarToken(User user, long minutos, String tipo) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        Instant exp = Instant.now().plusSeconds(minutos * 60);

        return JWT.create()
                .withIssuer(issuer)
                .withSubject(user.getEmail())
                .withClaim("uid", user.getId().toString())
                .withClaim("type", tipo)
                .withClaim("role", user.getRole().name())
                .withExpiresAt(exp)
                .sign(algorithm);
    }

    public DecodedJWT verify(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.require(algorithm).withIssuer(issuer).build().verify(token);
    }

    public String getSubject(String token) { return verify(token).getSubject(); }
    public String getTokenType(String token) { return verify(token).getClaim("type").asString(); }
}