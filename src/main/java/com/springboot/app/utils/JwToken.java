package com.springboot.app.utils;

import lombok.experimental.NonFinal;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.UUID;

@Component
public class JwToken {
    @NonFinal
    protected static final String SIGNER_KEY = "Ry5OGtcJ+5dTSUV30C1LMaNew2uOotz0zpaBT/F9DJ5fLbcC5EoYtK/Ldh3H8VZo";

    public Jwt jwtDecoder(String token) throws JwtException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(),"HS512");
        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build().decode(token);
    }
    public  String TokenConcat (String token) {
        return token.substring(7);
    }
    public UUID getIdFromToken(String token){
        var targetJWToken = jwtDecoder(TokenConcat(token));
        return UUID.fromString(targetJWToken.getClaim("Id"));
    }
    public String getRoleFromToken(String token){
        var targetJWToken = jwtDecoder(TokenConcat(token));
        return targetJWToken.getClaim("Role");
    }
}
