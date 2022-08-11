package com.jozz.venus.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
public class OpensearchConfiguration {

    @Value("${application.opensearch.config.hosts}")
    private String hosts;
    @Value("#{${application.opensearch.config.port}}")
    private Integer port;
    @Value("${application.opensearch.config.scheme}")
    private String scheme;
    @Value("${application.opensearch.config.userName}")
    private String userName;
    @Value("${application.opensearch.config.password}")
    private String password;

    @Value("#{${application.opensearch.config.connectTimeout}}")
    private Integer connectTimeout;
    @Value("#{${application.opensearch.config.socketTimeout}}")
    private Integer socketTimeout;
    @Value("#{${application.opensearch.config.connectionRequestTimeout}}")
    private Integer connectionRequestTimeout;
    @Value("#{${application.opensearch.config.maxConnTotal}}")
    private Integer maxConnTotal;
    @Value("#{${application.opensearch.config.maxConnPerRoute}}")
    private Integer maxConnPerRoute;

    @Bean
    public RestClient restClient(){
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(userName, password));
        final SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
            final MyHostnameVerifier hostnameVerifier = new MyHostnameVerifier();

            List<HttpHost> hostList = new ArrayList<>();
            Arrays.asList(hosts.split(",")).forEach(host -> {
                hostList.add(new HttpHost(host, port, scheme));
            });
            HttpHost[] httpHosts = hostList.toArray(new HttpHost[]{});

            RestClientBuilder builder = RestClient.builder(httpHosts);
            builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                    return httpClientBuilder
                            .setMaxConnTotal(maxConnTotal)
                            .setMaxConnPerRoute(maxConnPerRoute)
                            .setSSLContext(sslContext)
                            .setSSLHostnameVerifier(hostnameVerifier)
                            .setDefaultCredentialsProvider(credentialsProvider);
                }
            });
            builder.setRequestConfigCallback(requestConfigBuilder -> {
                requestConfigBuilder.setConnectTimeout(connectTimeout);
                requestConfigBuilder.setSocketTimeout(socketTimeout);
                requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeout);
                return requestConfigBuilder;
            });

            return builder.build();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void transport(){

    }
//    @Bean
//    public OpenSearchClient openSearchClient(){
//        RestClientTransport transport = new RestClientTransport(restClient(), new JacksonJsonpMapper());
//        return new OpenSearchClient(transport);
//    }

    private static class MyHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }

    private static class MyTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }


}

