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
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

@Service
public class AWSIotTLSUtils{
    private static final Logger log = LoggerFactory.getLogger(AWSIotTLSUtils.class);

    public static SSLSocketFactory getSocketFactory(String certPath, String keyPath, String caPath) throws Exception{

        //Validate caPath
        validateRootCAFile(caPath);

        //Load Ca Certificate
        log.info("Attempting to Load the Ca Certificate - AWSIotTLSUtils.java");
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        FileInputStream caInput = new FileInputStream(caPath);
        X509Certificate caCert = (X509Certificate) certFactory.generateCertificate(caInput);
        caInput.close();
        log.info("Successfully Loaded Ca Certificate on AWSIotTLSUtils.java");

        //Load Client Certificate
        log.info("Attempting to Load Client Certificate Line 36 AWSIotTLSUtils.java");
        InputStream certInput = new FileInputStream(certPath);
        X509Certificate clientCert = (X509Certificate) certFactory.generateCertificate(certInput);
        certInput.close();
        log.info("Successfully finished loading client Certificate line 40 AWSIotTLSUtils.java");

        //Load Private Key
        log.info("Attempting to load Private Key... line 43 AWSIotTLSUtils.java");
        PrivateKey privateKey = loadPrivateKey(keyPath);
        log.info("Successfully loaded Private Key line 45 AWSIotTLSUtils.java");

        //Create KeyStore and Load Cert + Private Key
        log.info("Attempting to Create KeyStore and Load Cert + Private Key... line 48 AWSIotTLSUtils.java");
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("cert", clientCert);
        keyStore.setKeyEntry("private-key", privateKey, new char[]{}, new Certificate[]{clientCert});
        log.info("Successfully Create KeyStore and Load Cert + Private Key line 53 AWSIotTLSUtils.java");

        //Create Trust Store and load CA Certificate
        log.info("Attempting to Create Trust Store and load CA Certificate... line 56 AWSIotTLSUtils.java");
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);
        trustStore.setCertificateEntry("ca-certificate", caCert);
        log.info("Successfully Create Trust Store and load CA Certificate line 60 AWSIotTLSUtils.java");

        //Key Manager
        log.info("Attempting to Make Key Manager... line 63 AWSIotTLSUtils.java");
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, new char[]{});
        log.info("Successfully Created Key Manager line 66 AWSIotTLSUtils.java");

        //Trust Manager
        log.info("Attempting to Make Trust Manager... line 69 AWSIotTLSUtils.java");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        log.info("Successfully Created Trust Manager line 72 AWSIotTLSUtils.java");

        //Create SSL Context
        log.info("Attempting to Create SSL Context... line 75 AWSIotTLSUtils.java");
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        log.info("Successfully Created SSL Context line 78 AWSIotTLSUtils.java");

        return sslContext.getSocketFactory();
    }

    private static PrivateKey loadPrivateKey(String keyPath) throws Exception{
        log.info("Attempting to Load Private Key... line 84 AWSIotTLSUtils.java");
        String key = new String(Files.readAllBytes(Paths.get(keyPath)))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\r", "")  // Remove Windows-style carriage returns
                .trim();
         //       .replaceAll("\\s+", "");

        //Comment our after debugging
        log.info("The length of the key is " + key.length());
        log.info(key);
        byte[] decodedKey = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        log.info("Successfully Loaded Private Key line 93 AWSIotTLSUtils.java");
        return keyFactory.generatePrivate(keySpec);
    }

    private static void validateRootCAFile(String caPath) {
        log.info("Attempting to Validate Root Ca Files... line 98 AWSIotTLSUtils.java");
        File caFile = new File(caPath);
        if (!caFile.exists()) {
            throw new RuntimeException("Root CA file missing at: " + caPath + ". Please download it before starting the application.");
        } else {
            log.info("Successfully Validated Root Ca Files line 93 AWSIotTLSUtils.java");
        }
    }
}