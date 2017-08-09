package com.sparrow.netty;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;

/**
 * Created by Administrator on 2016/11/14.
 */
public class SecureChatSslContextFactory {
    private static final String PROTOCOL = "SSL";
    private static final SSLContext SERVER_CONTEXT;
//    private static final SSLContext CLIENT_CONTEXT;


//    private static String CLIENT_KEY_STORE = "E:\\https\\client.keystore";
//    private static String CLIENT_TRUST_KEY_STORE = "E:\\https\\client.truststore";
//    private static String CLIENT_KEY_STORE_PASSWORD = "123456";
//    private static String CLIENT_TRUST_KEY_STORE_PASSWORD = "123456";


    private static String SERVER_KEY_STORE = "/security/server.ks";
    private static String SERVER_TRUST_KEY_STORE = "/security/trust.ks";
    private static String SERVER_KEY_STORE_PASSWORD = "yzc007";
    private static String SERVER_TRUST_KEY_STORE_PASSWORD = "yzc007";

    static {
        SSLContext serverContext;
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(SecureChatSslContextFactory.class.getResource(SERVER_KEY_STORE).openStream(), SERVER_KEY_STORE_PASSWORD.toCharArray());
            KeyStore tks = KeyStore.getInstance(KeyStore.getDefaultType());
            tks.load(SecureChatSslContextFactory.class.getResource(SERVER_TRUST_KEY_STORE).openStream(), SERVER_TRUST_KEY_STORE_PASSWORD.toCharArray());

            // Set up key manager factory to use our key store
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, SERVER_KEY_STORE_PASSWORD.toCharArray());
            tmf.init(tks);

            // Initialize the SSLContext to work with our key managers.
            serverContext = SSLContext.getInstance(PROTOCOL);
            serverContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        } catch (Exception e) {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        }

//        try {
//            KeyStore ks2 = KeyStore.getInstance("JKS");
//            ks2.load(new FileInputStream(CLIENT_KEY_STORE), CLIENT_KEY_STORE_PASSWORD.toCharArray());
//
//            KeyStore tks2 = KeyStore.getInstance("JKS");
//            tks2.load(new FileInputStream(CLIENT_TRUST_KEY_STORE), CLIENT_TRUST_KEY_STORE_PASSWORD.toCharArray());
//            // Set up key manager factory to use our key store
//            KeyManagerFactory kmf2 = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//            TrustManagerFactory tmf2 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            kmf2.init(ks2, CLIENT_KEY_STORE_PASSWORD.toCharArray());
//            tmf2.init(tks2);
//            clientContext = SSLContext.getInstance(PROTOCOL);
//            clientContext.init(kmf2.getKeyManagers(), tmf2.getTrustManagers(), null);
//        } catch (Exception e) {
//            throw new Error("Failed to initialize the client-side SSLContext", e);
//        }

        SERVER_CONTEXT = serverContext;
//        CLIENT_CONTEXT = clientContext;
    }


    public static SSLContext getServerContext() {
        return SERVER_CONTEXT;
    }


//    public static SSLContext getClientContext() {
//        return CLIENT_CONTEXT;
//    }

    private SecureChatSslContextFactory() {
        // Unused
    }
}
