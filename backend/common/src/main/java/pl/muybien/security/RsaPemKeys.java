package pl.muybien.security;

import org.springframework.util.StringUtils;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class RsaPemKeys {

    private RsaPemKeys() {}

    public static RSAPrivateKey readPrivateKey(String pem) {
        if (!StringUtils.hasText(pem)) throw new IllegalArgumentException("internal-jwt.private-key-pem is missing");
        byte[] der = Base64.getDecoder().decode(cleanPem(pem));
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(der));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid RSA private key PEM", e);
        }
    }

    public static RSAPublicKey readPublicKey(String pem) {
        if (!StringUtils.hasText(pem)) throw new IllegalArgumentException("internal-jwt.public-key-pem is missing");
        byte[] der = Base64.getDecoder().decode(cleanPem(pem));
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(der));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid RSA public key PEM", e);
        }
    }

    private static String cleanPem(String pem) {
        return pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
    }
}
