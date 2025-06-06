package com.fatec.backend.service.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fatec.backend.model.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.time.Instant.now;

@Service
public class JwtTokenService {
    @Value("${token.jwt.secret.key}")
    private String secretKey;
    @Value("${token.jwt.issuer}")
    private String issuer;

    public String generateToken(UserDetailsImpl user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.create()
                    .withIssuer(issuer)
                    .withIssuedAt(dataCriacao())
                    .withExpiresAt(dataExpiracao())
                    .withSubject(user.getUsername())
                    .sign(algorithm);
        }catch (JWTCreationException exception){
            throw new JWTCreationException("Erro ao gerar token",exception);
        }
    }

    public String pegarToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException exception){
            throw new JWTVerificationException("Token invalido ou expirado");
        }
    }
    public String generatePasswordResetToken(String email) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.create()
                    .withIssuer(issuer)
                    .withSubject(email)
                    .withClaim("type", "password-reset")
                    .withIssuedAt(now())
                    .withExpiresAt(dataExpiracaoRecuperacao())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new JWTCreationException("Erro ao gerar token de recuperação.", e);
        }
    }

    public String verifyPasswordResetToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.require(algorithm)
                    .withIssuer(issuer)
                    .withClaim("type", "password-reset")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException("Token de recuperação inválido ou expirado.");
        }
    }
    private Instant dataCriacao(){
        return ZonedDateTime
                .now(ZoneId.of("America/Sao_Paulo"))
                .toInstant();
    }
    private Instant dataExpiracao(){
        return ZonedDateTime
                .now(ZoneId.of("America/Sao_Paulo"))
                .plusHours(10)
                .toInstant();
    }
    private Instant dataExpiracaoRecuperacao(){
        return ZonedDateTime
                .now(ZoneId.of("America/Sao_Paulo"))
                .plusMinutes(20)
                .toInstant();
    }
}