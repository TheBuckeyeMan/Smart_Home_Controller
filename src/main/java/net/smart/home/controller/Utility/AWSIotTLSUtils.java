package net.smart.home.controller.Utility;

import javax.net.ssl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Service
public class AWSIotTLSUtils{
    private static final Logger log = LoggerFactory.getLogger(AWSIotTLSUtils.class);

    public static SSLSocketFactory getSocketFactory(String certPath, String keyPath, String caPath) throws Exception{
        //Load Ca Certificate
        log.info("Attempting to Load the Ca Certificate - AWSIotTLSUtils.java");
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        FileInputStream caInput = new FileInputStream(caPath);
        X509Certificate caCert = (X509Certificate) certFactory.generateCertificate(caInput);
        caInput.close();
        log.info("Successfully Loaded Ca Certificate on AWSIotTLSUtils.java");

        //Load Client Certificate
        InputStream certInput = new FileInputStream(certPath);
        X509Certificate clientCert = (X509Certificate) certFactory.generateCertificate(certInput);
        certInput.close();

        //Load Private Key
        PrivateKey privateKey = loadPrivateKey(keyPath);

        //Create KeyStore and Load Cert + Private Key
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("cert", clientCert);
        keyStore.setKeyEntry("private-key", privateKey, new char[]{}, new Certificate[]{clientCert});

        //Create Trust Store and load CA Certificate
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);
        trustStore.setCertificateEntry("ca-certificate", caCert);

        //Key Manager
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, new char[]{});

        //Trust Manager
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        //Create SSL Context
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return sslContext.getSocketFactory();
    }

    private static PrivateKey loadPrivateKey(String keyPath) throws Exception{
        String key = new String(Files.readAllBytes(Paths.get(keyPath)))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decodedKey = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
}