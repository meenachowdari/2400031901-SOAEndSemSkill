package com.klu.springboot.service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class JwtUtil {

    private static final String SECRET = System.getenv().getOrDefault(
            "JWT_SECRET",
            "lab-only-secret-key-change-this-123456789"
    );

    private JwtUtil() {}

    public static String generateToken(String username) throws Exception {
        String header = encode(
                "{\"alg\":\"HS256\",\"typ\":\"JWT\"}");

        long expiry = System.currentTimeMillis() / 1000 + 3600;

        String payload = encode(
                "{\"sub\":\"" + username + "\",\"exp\":" + expiry + "}");

        String content = header + "." + payload;

        return content + "." + sign(content);
    }

    private static String encode(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(
                value.getBytes(StandardCharsets.UTF_8));
    }

    private static String sign(String content) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(
                SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));

        return Base64.getUrlEncoder().withoutPadding().encodeToString(
                mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
    }

    public static boolean isValid(String token) {
        try {
            String[] parts = token.split("\\.", -1);
            if (parts.length != 3) {
                return false;
            }

            String expected = sign(parts[0] + "." + parts[1]);

            if (!java.security.MessageDigest.isEqual(
                    expected.getBytes(StandardCharsets.US_ASCII),
                    parts[2].getBytes(StandardCharsets.US_ASCII))) {
                return false;
            }

            String payload = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8);

            String subject = payload.split("\"sub\":\"", 2)[1]
                    .split("\"", 2)[0];

            long expiry = Long.parseLong(
                    payload.split("\"exp\":", 2)[1].split("[}]", 2)[0]);

            return !subject.isBlank()
                    && expiry > System.currentTimeMillis() / 1000;
        } catch (Exception e) {
            return false;
        }
    }
}