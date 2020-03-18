package com.atguigu.gmall0311.passport.util;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.RsaSigner;
import org.springframework.core.io.ClassPathResource;
import sun.security.rsa.RSASignature;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    public static String encode(String key, Map<String,Object> param, String salt){
        if(salt!=null){
            key+=salt;
        }
        JwtBuilder jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS256,key);

        jwtBuilder = jwtBuilder.setClaims(param);

        String token = jwtBuilder.compact();
        return token;

    }


    public  static Map<String,Object>  decode(String token ,String key,String salt){
        Claims claims=null;
        if (salt!=null){
            key+=salt;
        }
        try {
            claims= Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        } catch ( JwtException e) {
            return null;
        }
        return  claims;
    }

    public void keyStore() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        String key_location = "xc.keystore";
        String alias = "xckey";
        String keystore_password = "xuechengkeystore";
        String keypassword = "xuecheng";

        ClassPathResource resource=new ClassPathResource("xc.keyStore");
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(resource.getInputStream(), keystore_password.toCharArray());
        RSAPrivateKey key = (RSAPrivateKey) keyStore.getKey(alias, keypassword.toCharArray());
        RsaSigner rsaSigner = new RsaSigner(SignatureAlgorithm.RS256, key);
        String str = rsaSigner.toString();
        JwtBuilder jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.RS256,str);
        Map<String, Object>map=new HashMap<>();
        map.put("name","小仓朝日");
        jwtBuilder.setClaims(map);
        String token = jwtBuilder.compact();
        System.out.println(token);


    }


}
