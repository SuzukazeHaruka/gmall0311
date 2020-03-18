package com.atguigu.gmall0311.passport;


import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.RsaSigner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;
import sun.misc.BASE64Decoder;
import sun.security.rsa.RSAPublicKeyImpl;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GmallPassportWebApplicationTests {

    @Test
    public void keyStore() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        String key_location = "xc.keystore";
        String alias = "xckey";
        String keystore_password = "xuechengkeystore";
        String keypassword = "xuecheng";

        ClassPathResource resource=new ClassPathResource("xc.keyStore");
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(resource.getInputStream(), keystore_password.toCharArray());
        PrivateKey key = (PrivateKey) keyStore.getKey(alias, keypassword.toCharArray());
        JwtBuilder jwtBuilder = Jwts.builder();
        Map<String, Object> map=new HashMap<>();
        map.put("name","小仓朝日");
        jwtBuilder.setClaims(map);
        jwtBuilder.signWith(SignatureAlgorithm.RS256,key);
        String token = jwtBuilder.compact();
        System.out.println(token);


    }

    @Test
    public void deKeyStore() throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String token="eyJhbGciOiJSUzI1NiJ9.eyJuYW1lIjoi5bCP5LuT5pyd5pelIn0.UlNevGDYL9BJ0jnmLG5FaG4pox18LJ5-R3Ri1fMO8HyBAkHnMIlqApCiJCl25BpjeOALAEm-PQbJ-oweG05KUQTWjsnL5F4hukvrQZIN9SlICxKMDfK7NZ1bP_N_ZGY2SUS3vX4kYFvAU4vE_qro6XGxaJDbAUZhmElWBy6p1gdAKqOdnK_SUyep7Menz3WovVw6uDu3PsyFEIj-XVzTxAxOpSgCvKwj5ZTvqiCDOxi62ABcSw-m3AhJK3yk_7iVCePSIWp1pOeyQGm11FAFuvUHuIgDrEQLbVX3Aqffe89I5FuzC0GBbchwEi9dFs2GxY6qPR9zUsVTZCBZ8oJYsg";
        String public_key="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl5uAktA1+Uh7U4Ji4PYH7auL32P07N+qvEmEBhGCp+lllXHAMlKUTwt3FCEA5qcCHN+rdJT5bVgtSxT4/3GquYj6bM95GpXz2zj5wTYYksGWJebHnscHbUamyNHNR+ek3mViQerdfDr6yMcEQpXc8GEm9qTxWMVS1yBLN7KMUIBL+w74bje8puDkfFdBw4vyLmeoxF5XEgrrEWVJWR89JAx1E0D0P5ubLWkKmdfxPoOjS2vXYJ4H98B9+yNn04SbyxXeHwECmKZkbNUDhozgWvei79w2J98UOX6GGe1Y94th4i/j0LPm96Uu0gZldpzGNfRgz8TkKFW4WfugeJYXwQIDAQAB";
        // 通过公钥解析Token
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec x509EncodedKeySpec=new X509EncodedKeySpec( new BASE64Decoder().decodeBuffer(public_key));
       PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Jws<Claims> claimsJws = Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(token);
        Claims body = claimsJws.getBody();
        String name = (String) body.get("name");
        System.out.println(name);
    }

}
