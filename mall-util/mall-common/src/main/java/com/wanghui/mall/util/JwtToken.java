package com.wanghui.mall.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class JwtToken {

    // Default secret key
    private static final String DEFAULT_SECRET="springcloudalibaba";

    /***
     * Create JWT token
     * Secret key: secret
     * Payload: dataMap(Map)
     */
    public static String createToken(Map<String,Object> dataMap){
        return createToken(dataMap,null);
    }
    /***
     * Create JWT token
     * Secret key: secret
     * Payload: dataMap(Map)
     */
    public static String createToken(Map<String,Object> dataMap, String secret){
        // Confirm secret key
        if(StringUtils.isEmpty(secret)){
            secret = DEFAULT_SECRET;
        }

        // Confirm signature algorithm
        Algorithm algorithm = Algorithm.HMAC256(secret);

        // JWT token creation
        return
        JWT.create()
                .withClaim("body",dataMap)  // Custom payload
                .withIssuer("GP")   // Issuer
                .withSubject("JWT Token")   // Subject
                .withAudience("member") // Audience
                .withExpiresAt(new Date(System.currentTimeMillis()+3600000))    // Expiration time
                .withNotBefore(new Date(System.currentTimeMillis()+1000))       // Available after 1 second
                .withIssuedAt(new Date())   // Issue time
                .withJWTId(UUID.randomUUID().toString().replace("-",""))    // Unique identifier
                .sign(algorithm);
    }

    /****
     * Parse token
     */
    public static Map<String,Object> parseToken(String token){
        return parseToken(token,null);
    }
    /****
     * Parse token
     */
    public static Map<String,Object> parseToken(String token,String secret){
        // Confirm secret key
        if(StringUtils.isEmpty(secret)){
            secret = DEFAULT_SECRET;
        }

        // Confirm signature algorithm
        Algorithm algorithm = Algorithm.HMAC256(secret);

        // Create token verifier
        JWTVerifier verifier = JWT.require(algorithm).build();
        // Verify and parse
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("body").as(Map.class);
    }

    public static void main(String[] args) throws InterruptedException {
        // Create token
        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put("name","zhangsan");
        dataMap.put("address","湖南");

        // Create token
        String token = createToken(dataMap);
        System.out.println(token);

        // Sleep for one second
        TimeUnit.SECONDS.sleep(1);

        // Verify and parse token
        Map<String, Object> stringObjectMap = parseToken(token);
        System.out.println(stringObjectMap);
    }
}
