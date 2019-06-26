package com.spring.pro.hellospring.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.springframework.util.StringUtils;

import javax.net.ssl.*;
import javax.swing.text.Document;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * @Author: xiang.zhao
 * @ClassName:
 * @Description:
 * @Date: 2019/6/21 10:16
 */
public class HTTPCommonUtil {
    public static void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            } }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getHttpHeaders(URL url, int timeout) {
        try {
            trustEveryone();
            Connection conn = HttpConnection.connect(url);
            conn.timeout(timeout);
            conn.header("Accept-Encoding", "gzip,deflate,sdch");
            conn.header("Connection", "close");
            conn.get();
            Map<String, String> result = conn.response().headers();
            result.put("title", conn.response().parse().title());
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            String tweet = "";
            System.out.println(StringUtils.isEmpty(tweet) ? tweet : tweet.replaceAll("\\)", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
