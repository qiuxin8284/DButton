package com.sfr.dbuttonapplication.http;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * ssl factory Ϊhttps����
 *
 * @author michael.cui
 */

public class SSLSocketFactoryEx extends SSLSocketFactory {
        
        SSLContext sslContext = SSLContext.getInstance("TLS");
        
        public SSLSocketFactoryEx(KeyStore truststore)
                        throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
                super(truststore);
                
                TrustManager tm = new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {return null;}
    
            @Override
            public void checkClientTrusted(
                    X509Certificate[] chain, String authType)
                                            throws java.security.cert.CertificateException {
                if (null == chain || chain.length <= 0) {
                    throw new IllegalArgumentException("checkClientTrusted: X509Certificate array is null or empty");
                } else {

                    for (X509Certificate current_chain : chain) {
                        try {
                            current_chain.checkValidity();
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new IllegalArgumentException("checkClientTrusted: X509Certificate error");
                        }
                    }
                }
                android.util.Log.e("SlashInfo", "checkClientTrusted===key= " + authType + " default= " + KeyStore.getDefaultType());
                if (!(null != authType && authType.equalsIgnoreCase("RSA"))) {

                    throw new IllegalArgumentException("checkClientTrusted: AuthType is not RSA");

                }
            }
    
            @Override
            public void checkServerTrusted(
                    X509Certificate[] chain, String authType)
                                            throws java.security.cert.CertificateException {
                if (null == chain || chain.length <= 0) {
                    throw new IllegalArgumentException("checkServerTrusted: X509Certificate array is null or empty");
                } else {

                    for (X509Certificate current_chain : chain) {
                        try {
                            current_chain.checkValidity();
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new IllegalArgumentException("checkServerTrusted: X509Certificate error");
                        }
                    }
                }
                android.util.Log.e("SlashInfo", "checkServerTrusted===key= " + authType + " default= " + KeyStore.getDefaultType());
                if (!(null != authType && authType.equalsIgnoreCase("RSA"))) {

                    throw new IllegalArgumentException("checkServerTrusted: AuthType is not RSA");

                }
            }
        };  
        sslContext.init(null, new TrustManager[] { tm }, null);
    }  
    
    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port,autoClose);  
    }  
    
    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();  
    }  
}
