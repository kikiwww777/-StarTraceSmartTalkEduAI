package com.eduai.Service.impl;

import com.eduai.Service.SimInternalJwtService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * Sim Internal JWT 生成器（HS256）
 *
 * 对齐 Sim 源码：
 * - payload: { type: "internal", userId }
 * - issuer: "sim-internal"
 * - audience: "sim-api"
 * - exp: 5m
 */
@Service
public class SimInternalJwtServiceImpl implements SimInternalJwtService {

    @Value("${sim.internal.api-secret:}")
    private String internalApiSecret;

    @Override
    public String generate(String simUserId) {
        if (internalApiSecret == null || internalApiSecret.trim().isEmpty()) {
            throw new IllegalStateException("sim.internal.api-secret 未配置");
        }
        if (simUserId == null || simUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("simUserId 不能为空");
        }

        try {
            byte[] secretBytes = internalApiSecret.getBytes(StandardCharsets.UTF_8);
            MACSigner signer = new MACSigner(secretBytes);

            Instant now = Instant.now();
            Date issueTime = Date.from(now);
            Date expTime = Date.from(now.plusSeconds(5 * 60));

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .claim("type", "internal")
                    .claim("userId", simUserId)
                    .issuer("sim-internal")
                    .audience("sim-api")
                    .issueTime(issueTime)
                    .expirationTime(expTime)
                    .build();

            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            jwt.sign(signer);
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("生成 Sim Internal JWT 失败: " + e.getMessage(), e);
        }
    }
}


