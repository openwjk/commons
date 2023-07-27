package com.openwjk.commons.utils;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.ImmutableMap;
import com.openwjk.commons.exception.CommonsException;
import com.openwjk.commons.exception.HttpUnauthorizedException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.rmi.RemoteException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
    private static final Logger desLogger = LoggerFactory.getLogger("DesLogger");
    private static final String DEFAULT_CHARSET = "UTF-8";

    // 设置请求超时，默认30秒
    private static final int SO_TIMEOUT = 30000;

    // 设置连接超时时间，单位毫秒，默认30秒
    private static final int CONNECTION_TIMEOUT = 30000;

    // 设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的
    private static final int CONNECTION_REQUEST_TIMEOUT = 30000;

    // 设置文件上传来连接超时时间，单位毫秒，默认120秒
    private static final int FILEUPLOAD_CONNECTION_TIMEOUT = 120000;

    // 设置从connect Manager获取Connection 超时时间，单位毫秒。
    private static final int FILEUPLOAD_CONNECTION_REQUEST_TIMEOUT = 120000;

    // 客户端链接默认超时时间，单位毫秒。
    private static final Long HOST_KEEPALIVE_STRATEGY_TIMEOUT = 30000L;

    private static PoolingHttpClientConnectionManager connManager = null;
    private static CloseableHttpClient httpclient = null;
    private static ConnectionKeepAliveStrategy defaultKeepAliveStrategy = null;

    static {
        try {
            defaultKeepAliveStrategy = new ConnectionKeepAliveStrategy() {
                @Override
                public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                    return HOST_KEEPALIVE_STRATEGY_TIMEOUT;
                }
            };

            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", sslsf).build();

            connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            httpclient = HttpClients.custom().setConnectionManager(connManager).setKeepAliveStrategy(defaultKeepAliveStrategy).build();
            // Create socket configuration
            SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(CONNECTION_TIMEOUT).setTcpNoDelay(true).build();
            connManager.setDefaultSocketConfig(socketConfig);

            // Create connection configuration
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setMalformedInputAction(CodingErrorAction.IGNORE)
                    .setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8)
                    .build();
            connManager.setDefaultConnectionConfig(connectionConfig);
            connManager.setMaxTotal(500);
            connManager.setDefaultMaxPerRoute(50);
        } catch (KeyManagementException e) {
            logger.error("KeyManagementException", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("NoSuchAlgorithmException", e);
        } catch (GeneralSecurityException e) {
            logger.error("GeneralSecurityException", e);
        }
    }

    /**
     * HTTP请求，默认超时为5S
     *
     * @param reqURL
     * @param params
     * @return
     */
    public static String httpPostWithForm(String reqURL, Map<String, String> params) {
        return httpPostWithForm(reqURL, params, DEFAULT_CHARSET, true);
    }

    public static String httpPostWithForm(String reqURL, Map<String, String> params, boolean printParam) {
        return httpPostWithForm(reqURL, params, DEFAULT_CHARSET, printParam);
    }

    @SneakyThrows
    public static String httpPostWithForm(String reqURL, Map<String, String> params, String charSet, boolean printParam) {
        long start = System.currentTimeMillis();

        final CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(connManager)
                .setKeepAliveStrategy(defaultKeepAliveStrategy)
                .build();

        String responseContent = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;

        HttpPost httpPost = new HttpPost(reqURL);
        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(SO_TIMEOUT)
                    .setConnectTimeout(CONNECTION_TIMEOUT)
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .build();
            List<NameValuePair> formParams = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, charSet));
            httpPost.setConfig(requestConfig);
            if (printParam) {
                desLogger.info("request url: {}, params: {}", reqURL, params);
            }
            desLogger.info("request url: {}", reqURL);

            response = httpclient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            desLogger.info("request url: {}, statusCode: {}", reqURL, statusCode);

            // 获取响应实体
            entity = response.getEntity();
            if (null != entity) {
                responseContent = EntityUtils.toString(entity, charSet);
            }
            desLogger.info("request url: {}, responseContent: {}", reqURL, responseContent);

            if (statusCode != HttpStatus.SC_OK) {
                throw new RemoteException(
                        String.format("request url: %s, response status error, statusCode: %s", reqURL, statusCode));
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            desLogger.info(String.format("request url: %s, error params: %s", reqURL, params));
            desLogger.info(String.format("request url: %s, param error", reqURL));
            throw new CommonsException(String.format("request url: %s.", reqURL), e);
        } finally {
            closeConnection(entity, response, httpPost);
            desLogger.info("request url: {} costs {}ms", reqURL, System.currentTimeMillis() - start);
        }
        return responseContent;
    }

    public static String httpPost(String url, String bodyParam, String charset) {
        ContentType contentType = ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), charset);
        return httpPost(url, bodyParam, contentType);
    }

    public static String httpPost(String url, String bodyParam, ContentType contentType) {
        return httpPost(url, null, bodyParam, contentType);
    }

    public static String httpPost(String url, String bodyParam, ContentType contentType, boolean printParam) {
        return httpPost(url, null, bodyParam, null, contentType, printParam);
    }

    private static String connectUrlParam(String url, Map<String, String> urlParam) throws UnsupportedEncodingException {
        if (MapUtils.isEmpty(urlParam)) {
            return url;
        }
        StringBuilder connectParam = new StringBuilder();
        Boolean paramFlag = false;
        if (url.contains("?")) {
            paramFlag = true;
        }
        if (paramFlag) {
            connectParam.append("&");
        } else {
            connectParam.append("?");
        }
        for (Map.Entry<String, String> entry : urlParam.entrySet()) {
            connectParam.append(entry.getKey());
            connectParam.append("=");
            connectParam.append(URLEncoder.encode(entry.getValue(), DEFAULT_CHARSET));
            connectParam.append("&");
        }
        connectParam.deleteCharAt(connectParam.length() - 1);
        return url + connectParam;
    }

    @SneakyThrows
    public static String httpPost(String url, List<NameValuePair> headList, String bodyParam,
                                  Map<String, String> urlParam, ContentType contentType, boolean printParam) {
        if (StringUtils.isEmpty(url)) {
            throw new CommonsException("url is required param");
        }


        final CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(connManager)
                .setKeepAliveStrategy(defaultKeepAliveStrategy)
                .build();

        HttpEntity entity = null;
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        long start = System.currentTimeMillis();

        try {
            String processedUrl = connectUrlParam(url, urlParam);

            httpPost = new HttpPost(processedUrl);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(CONNECTION_TIMEOUT)
                    .setConnectTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .build();// 设置请求和传输超时时间
            httpPost.setConfig(requestConfig);
            if (CollectionUtils.isNotEmpty(headList)) {
                for (NameValuePair head : headList) {
                    httpPost.addHeader(head.getName(), head.getValue());
                }
            }
            if (StringUtils.isNotEmpty(bodyParam)) {
                httpPost.setEntity(new StringEntity(bodyParam, contentType));
            }
            if (printParam) {
                desLogger.info("request url: {} begin, params: {}", processedUrl, bodyParam);
            }
            desLogger.info("request url: {} begin", processedUrl);
            response = httpclient.execute(httpPost);
            desLogger.info("request url: {} end, get statusCode: {}",
                    processedUrl, response.getStatusLine().getStatusCode());

            String charset = DEFAULT_CHARSET;
            if (null != contentType.getCharset()) {
                charset = contentType.getCharset().name();
            }
            entity = response.getEntity();
            String respStr = EntityUtils.toString(entity, charset);
            desLogger.info("request url: {}, get responseContent: {}", processedUrl, respStr);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                throw new HttpUnauthorizedException();
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new RemoteException(
                        String.format("request url: %s, response status error, statusCode: %s",
                                processedUrl, statusCode));
            }
            return respStr;
        } catch (HttpUnauthorizedException | RemoteException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonsException(e.getMessage(), e);
        } finally {
            closeConnection(entity, response, httpPost);
            desLogger.info("request url: {} costs {}ms", url, System.currentTimeMillis() - start);
        }
    }

    @SneakyThrows
    public static ImmutableMap<String, Object> httpPostHttpResponse(String url, List<NameValuePair> headList, String bodyParam, ContentType contentType) {
        if (StringUtils.isEmpty(url)) {
            throw new CommonsException("url is required param");
        }

        final CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(connManager)
                .setKeepAliveStrategy(defaultKeepAliveStrategy)
                .build();

        HttpEntity entity = null;
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        long start = System.currentTimeMillis();

        try {
            String processedUrl = connectUrlParam(url, null);

            httpPost = new HttpPost(processedUrl);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(CONNECTION_TIMEOUT)
                    .setConnectTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .build();// 设置请求和传输超时时间
            httpPost.setConfig(requestConfig);
            if (CollectionUtils.isNotEmpty(headList)) {
                for (NameValuePair head : headList) {
                    httpPost.addHeader(head.getName(), head.getValue());
                }
            }
            if (StringUtils.isNotEmpty(bodyParam)) {
                httpPost.setEntity(new StringEntity(bodyParam, contentType));
            }
            desLogger.info("request url: {} begin, params: {}", processedUrl, bodyParam);
            response = httpclient.execute(httpPost);
            desLogger.info("request url: {} end, get statusCode: {}",
                    processedUrl, response.getStatusLine().getStatusCode());

            String charset = DEFAULT_CHARSET;
            if (null != contentType.getCharset()) {
                charset = contentType.getCharset().name();
            }
            entity = response.getEntity();
            String respStr = EntityUtils.toString(entity, charset);
            desLogger.info("request url: {}, get responseContent: {}", processedUrl, respStr);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                throw new HttpUnauthorizedException();
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new RemoteException(
                        String.format("request url: %s, response status error, statusCode: %s",
                                processedUrl, statusCode));
            }
            return ImmutableMap.of("response", response, "entity", respStr);
        } catch (HttpUnauthorizedException | RemoteException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonsException(e.getMessage(), e);
        } finally {
            closeConnection(entity, response, httpPost);
            desLogger.info("request url: {} costs {}ms", url, System.currentTimeMillis() - start);
        }
    }

    public static String httpPostWithKeepAliveStrategy(String url, String bodyParam, ContentType contentType) {
        return httpPostWithKeepAliveStrategy(url, null, bodyParam, contentType);
    }

    public static String httpPostWithKeepAliveStrategy(String url, List<NameValuePair> headList, String bodyParam, ContentType contentType) {
        return httpPostWithKeepAliveStrategy(url, headList, bodyParam, null, contentType, true);
    }

    @SneakyThrows
    public static String httpPostWithKeepAliveStrategy(String url, List<NameValuePair> headList, String bodyParam,
                                                       Map<String, String> urlParam, ContentType contentType, boolean printParam) {
        if (StringUtils.isEmpty(url)) {
            throw new CommonsException("url is required param");
        }

        CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(connManager)
                .setKeepAliveStrategy(defaultKeepAliveStrategy)
                .build();
        HttpEntity entity = null;
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        long start = System.currentTimeMillis();

        try {
            String processedUrl = connectUrlParam(url, urlParam);

            httpPost = new HttpPost(processedUrl);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(CONNECTION_TIMEOUT)
                    .setConnectTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .build();// 设置请求和传输超时时间
            httpPost.setConfig(requestConfig);
            if (CollectionUtils.isNotEmpty(headList)) {
                for (NameValuePair head : headList) {
                    httpPost.addHeader(head.getName(), head.getValue());
                }
            }
            if (StringUtils.isNotEmpty(bodyParam)) {
                httpPost.setEntity(new StringEntity(bodyParam, contentType));
            }
            if (printParam) {
                desLogger.info("request url: {} begin, params: {}", processedUrl, bodyParam);
            }
            desLogger.info("request url: {} begin", processedUrl);
            response = httpclient.execute(httpPost);
            desLogger.info("request url: {} end, get statusCode: {}",
                    processedUrl, response.getStatusLine().getStatusCode());

            String charset = DEFAULT_CHARSET;
            if (null != contentType.getCharset()) {
                charset = contentType.getCharset().name();
            }
            entity = response.getEntity();
            String respStr = EntityUtils.toString(entity, charset);
            desLogger.info("request url: {}, get responseContent: {}", processedUrl, respStr);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                throw new HttpUnauthorizedException();
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new RemoteException(
                        String.format("request url: %s, response status error, statusCode: %s",
                                processedUrl, statusCode));
            }
            return respStr;
        } catch (HttpUnauthorizedException | RemoteException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonsException(e.getMessage(), e);
        } finally {
            closeConnection(entity, response, httpPost);
            desLogger.info("request url: {} costs {}ms", url, System.currentTimeMillis() - start);
        }
    }

    @SneakyThrows
    public static String httpPostFixedHeader(String url, List<NameValuePair> headList, String bodyParam,
                                             Map<String, String> urlParam, ContentType contentType, boolean printParam) {
        if (StringUtils.isEmpty(url)) {
            throw new CommonsException("url is required param");
        }


        final CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(connManager)
                .setKeepAliveStrategy(defaultKeepAliveStrategy)
                .build();
        HttpEntity entity = null;
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        long start = System.currentTimeMillis();

        try {
            String processedUrl = connectUrlParam(url, urlParam);

            httpPost = new HttpPost(processedUrl);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(CONNECTION_TIMEOUT)
                    .setConnectTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .build();// 设置请求和传输超时时间
            httpPost.setConfig(requestConfig);
            httpPost.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
            if (CollectionUtils.isNotEmpty(headList)) {
                for (NameValuePair head : headList) {
                    httpPost.addHeader(head.getName(), head.getValue());
                }
            }
            if (StringUtils.isNotEmpty(bodyParam)) {
                httpPost.setEntity(new StringEntity(bodyParam, contentType));
            }
            if (printParam) {
                desLogger.info("request url: {} begin, params: {}", processedUrl, bodyParam);
            }
            desLogger.info("request url: {} begin", processedUrl);
            response = httpclient.execute(httpPost);
            desLogger.info("request url: {} end, get statusCode: {}",
                    processedUrl, response.getStatusLine().getStatusCode());

            String charset = DEFAULT_CHARSET;
            if (null != contentType.getCharset()) {
                charset = contentType.getCharset().name();
            }
            entity = response.getEntity();
            String respStr = EntityUtils.toString(entity, charset);
            desLogger.info("request url: {}, get responseContent: {}", processedUrl, respStr);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                throw new HttpUnauthorizedException();
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new RemoteException(
                        String.format("request url: %s, response status error, statusCode: %s",
                                processedUrl, statusCode));
            }
            return respStr;
        } catch (HttpUnauthorizedException | RemoteException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonsException(e.getMessage(), e);
        } finally {
            closeConnection(entity, response, httpPost);
            desLogger.info("request url: {} costs {}ms", url, System.currentTimeMillis() - start);
        }
    }

    public static String httpPost(String url, List<NameValuePair> headList, String bodyParam, ContentType contentType) {
        return httpPost(url, headList, bodyParam, null, contentType, true);
    }

    public static String httpPostFixedHeader(String url, List<NameValuePair> headList, String bodyParam, ContentType contentType) {
        return httpPostFixedHeader(url, headList, bodyParam, null, contentType, true);
    }

    @SneakyThrows
    public static String httpGet(String url, List<NameValuePair> headList, Map<String, String> urlParam) {
        if (StringUtils.isEmpty(url)) {
            throw new CommonsException("url is required param");
        }


        final CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(connManager)
                .setKeepAliveStrategy(defaultKeepAliveStrategy)
                .build();
        HttpEntity entity = null;
        HttpGet httpGet = null;
        CloseableHttpResponse response = null;
        long start = System.currentTimeMillis();
        try {
            String processedUrl = connectUrlParam(url, urlParam);
            httpGet = new HttpGet(processedUrl);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(CONNECTION_TIMEOUT)
                    .setConnectTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .build();// 设置请求和传输超时时间
            httpGet.setConfig(requestConfig);
            if (CollectionUtils.isNotEmpty(headList)) {
                for (NameValuePair head : headList) {
                    httpGet.addHeader(head.getName(), head.getValue());
                }
            }

            desLogger.info("request url: {} begin", processedUrl);
            response = httpclient.execute(httpGet);
            desLogger.info("request url: {} end, get statusCode: {}",
                    processedUrl, response.getStatusLine().getStatusCode());
            entity = response.getEntity();
            String respStr = EntityUtils.toString(entity, DEFAULT_CHARSET);
            desLogger.info("request url: {}, get responseContent: {}", processedUrl, respStr);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                throw new HttpUnauthorizedException();
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new RemoteException(
                        String.format("request url: %s, response status error, statusCode: %s",
                                processedUrl, statusCode));
            }
            return respStr;
        } catch (HttpUnauthorizedException | RemoteException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonsException(e.getMessage(), e);
        } finally {
            closeConnection(entity, response, httpGet);
            desLogger.info("request url: {} costs {}ms", url, System.currentTimeMillis() - start);
        }
    }

    /**
     * HttpPost请求上传文件，默认超时2min
     *
     * @param url
     * @param headList
     * @param urlParam
     * @return
     */
    @SneakyThrows
    public static String httpPostWithFile(String url, List<NameValuePair> headList, Map<String, Object> params,
                                          Map<String, String> urlParam) {
        if (StringUtils.isEmpty(url)) {
            throw new CommonsException("url is required param");
        }

        if (MapUtils.isEmpty(params)) {
            throw new CommonsException("params is required param");
        }
        final CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(connManager)
                .setKeepAliveStrategy(defaultKeepAliveStrategy)
                .build();

        HttpEntity entity = null;
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        long start = System.currentTimeMillis();

        try {
            String processedUrl = connectUrlParam(url, urlParam);

            httpPost = new HttpPost(processedUrl);

            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(FILEUPLOAD_CONNECTION_TIMEOUT)
                    .setSocketTimeout(FILEUPLOAD_CONNECTION_REQUEST_TIMEOUT)
                    .build();// 设置请求和传输超时时间
            httpPost.setConfig(requestConfig);
            if (CollectionUtils.isNotEmpty(headList)) {
                for (NameValuePair head : headList) {
                    httpPost.addHeader(head.getName(), head.getValue());
                }
            }
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.RFC6532);
            multipartEntityBuilder.setCharset(Charset.forName(DEFAULT_CHARSET));
            multipartEntityBuilder.setContentType(ContentType.MULTIPART_FORM_DATA);
            for (Map.Entry entry : params.entrySet()) {
                if (entry.getValue() instanceof File) {
                    FileBody bin = new FileBody((File) entry.getValue());
                    multipartEntityBuilder.addPart(String.valueOf(entry.getKey()), bin).setCharset(Charset.forName("UTF-8"));
                } else {
                    multipartEntityBuilder.addTextBody(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())).setCharset(Charset.forName("UTF-8"));
                }

            }
            httpPost.setEntity(multipartEntityBuilder.build());
            desLogger.info("executing request " + httpPost.getRequestLine());
            response = httpclient.execute(httpPost);
            desLogger.info("request url: {} end, get statusCode: {}",
                    processedUrl, response.getStatusLine().getStatusCode());

            String charset = DEFAULT_CHARSET;
            entity = response.getEntity();
            String respStr = EntityUtils.toString(entity, charset);
            desLogger.info("request url: {}, get responseContent: {}", processedUrl, respStr);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                throw new HttpUnauthorizedException();
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new RemoteException(
                        String.format("request url: %s, response status error, statusCode: %s",
                                processedUrl, statusCode));
            }
            return respStr;
        } catch (HttpUnauthorizedException | RemoteException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonsException(e.getMessage(), e);
        } finally {
            closeConnection(entity, response, httpPost);
            desLogger.info("request url: {} costs {}ms", url, System.currentTimeMillis() - start);
        }
    }

    private static void closeConnection(HttpEntity entity, CloseableHttpResponse response, HttpPost httpPost) {
        try {
            if (entity != null) {
                entity.getContent().close();
            }
            if (response != null) {
                response.close();
            }
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        if (httpPost != null) {
            httpPost.releaseConnection();
        }
    }

    private static void closeConnection(HttpEntity entity, CloseableHttpResponse response, HttpGet httpGet) {
        try {
            if (entity != null) {
                entity.getContent().close();
            }
            if (response != null) {
                response.close();
            }
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        if (httpGet != null) {
            httpGet.releaseConnection();
        }
    }

    public static String httpPostWithSSL(String url, String bodyParam, SSLContext sslcontext) {
        return httpPostWithSSL(url, null, bodyParam, sslcontext);
    }

    public static String httpPostWithSSL(String url, Map<String, String> urlParam, String bodyParam, SSLContext sslcontext) {
        return httpPostWithSSL(url, null, urlParam, bodyParam, sslcontext);
    }

    @SneakyThrows
    public static String httpPostWithSSL(String url, List<NameValuePair> headList, Map<String, String> urlParam, String bodyParam, SSLContext sslcontext) {

        long start = System.currentTimeMillis();

        HttpEntity entity = null;
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;

        try {

            //  ssl socket factory
            final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[]{"TLSv1.1", "TLSv1.2"},
                    null,
                    // 不校验服务器域名
                    new NoopHostnameVerifier());

            // httClient 实例
            final CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .setKeepAliveStrategy(defaultKeepAliveStrategy)
                    .build();

            String processedUrl = connectUrlParam(url, urlParam);
            httpPost = new HttpPost(processedUrl);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(FILEUPLOAD_CONNECTION_TIMEOUT)
                    .setSocketTimeout(FILEUPLOAD_CONNECTION_REQUEST_TIMEOUT)
                    .build();// 设置请求和传输超时时间
            httpPost.setConfig(requestConfig);

            if (CollectionUtils.isNotEmpty(headList)) {
                for (NameValuePair head : headList) {
                    httpPost.addHeader(head.getName(), head.getValue());
                }
            }

            if (StringUtils.isNotEmpty(bodyParam)) {
                httpPost.setEntity(new StringEntity(bodyParam, ContentType.APPLICATION_JSON));
            }

            response = httpClient.execute(httpPost);
            desLogger.info("request url: {} end, get statusCode: {}",
                    url, response.getStatusLine().getStatusCode());

            entity = response.getEntity();
            String respStr = EntityUtils.toString(entity, "utf-8");
            desLogger.info("request url: {}, get responseContent: {}", url, respStr);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                throw new HttpUnauthorizedException();
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new RemoteException(
                        String.format("request url: %s, response status error, statusCode: %s",
                                url, statusCode));
            }
            return respStr;
        } catch (HttpUnauthorizedException | RemoteException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonsException(e.getMessage(), e);
        } finally {
            closeConnection(entity, response, httpPost);
            desLogger.info("request url: {} costs {}ms", url, System.currentTimeMillis() - start);
        }
    }


    @SneakyThrows
    public static String httpPostWithFileBody(String url, List<NameValuePair> headList, Map<String, Object> params,
                                              Map<String, String> urlParam) {
        if (StringUtils.isEmpty(url)) {
            throw new CommonsException("url is required param");
        }
        if (MapUtils.isEmpty(params)) {
            throw new CommonsException("params is required param");
        }
        final CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(connManager)
                .setKeepAliveStrategy(defaultKeepAliveStrategy)
                .build();

        HttpEntity entity = null;
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        long start = System.currentTimeMillis();

        try {
            String processedUrl = connectUrlParam(url, urlParam);

            httpPost = new HttpPost(processedUrl);

            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(FILEUPLOAD_CONNECTION_TIMEOUT)
                    .setSocketTimeout(FILEUPLOAD_CONNECTION_REQUEST_TIMEOUT)
                    .build();// 设置请求和传输超时时间
            httpPost.setConfig(requestConfig);
            if (CollectionUtils.isNotEmpty(headList)) {
                for (NameValuePair head : headList) {
                    httpPost.addHeader(head.getName(), head.getValue());
                }
            }
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
//            multipartEntityBuilder.setCharset(StandardCharsets.UTF_8);
            multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            desLogger.info("request param {}", JSON.toJSONString(params));
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() instanceof File) {
                    FileBody bin = new FileBody((File) entry.getValue());
                    multipartEntityBuilder.addPart(entry.getKey(), bin);
                } else if (entry.getValue() instanceof String) {
                    multipartEntityBuilder.addPart(entry.getKey(), new StringBody((String) entry.getValue(), ContentType.create("multipart/form-data", Consts.UTF_8)));
                } else {
                    multipartEntityBuilder.addTextBody(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())).setCharset(Charset.forName("UTF-8"));
                }

            }
            httpPost.setEntity(multipartEntityBuilder.build());
            desLogger.info("executing request " + httpPost.getRequestLine());
            response = httpclient.execute(httpPost);
            desLogger.info("request url: {} end, get statusCode: {}",
                    processedUrl, response.getStatusLine().getStatusCode());

            String charset = DEFAULT_CHARSET;
            entity = response.getEntity();
            String respStr = EntityUtils.toString(entity, charset);
            desLogger.info("request url: {}, get responseContent: {}", processedUrl, respStr);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                throw new HttpUnauthorizedException();
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new RemoteException(
                        String.format("request url: %s, response status error, statusCode: %s",
                                processedUrl, statusCode));
            }
            return respStr;
        } catch (HttpUnauthorizedException | RemoteException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonsException(e.getMessage(), e);
        } finally {
            closeConnection(entity, response, httpPost);
            desLogger.info("request url: {} costs {}ms", url, System.currentTimeMillis() - start);
        }
    }

    /**
     * 获取超时时间策略配置
     *
     * @return
     */

    public static SSLContext getSSLContext(String password, String sslPath) {
        try (InputStream fis = new FileInputStream(sslPath)) {
            final KeyStore keystore = KeyStore.getInstance("PKCS12");
            keystore.load(fis, password.toCharArray());
            // ssl context
            return SSLContexts.custom()
                    .loadKeyMaterial(keystore, password.toCharArray())
                    // 信任任何服务器证书
                    .loadTrustMaterial(new TrustAllStrategy())
                    .build();
        } catch (Exception e) {
            throw new CommonsException(e.getMessage(), e);
        }
    }

    /**
     * HTTP请求，默认超时为5S
     *
     * @param reqURL
     * @param params
     * @return
     */
    public static String httpPostWithForm(String reqURL, List<NameValuePair> headList, Map<String, String> params) {
        return httpPostWithForm(reqURL, headList, params, DEFAULT_CHARSET, true);
    }

    @SneakyThrows
    public static String httpPostWithForm(String reqURL, List<NameValuePair> headList, Map<String, String> params, String charSet, boolean printParam) {
        long start = System.currentTimeMillis();

        final CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(connManager)
                .setKeepAliveStrategy(defaultKeepAliveStrategy)
                .build();

        String responseContent = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;

        HttpPost httpPost = new HttpPost(reqURL);
        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(SO_TIMEOUT)
                    .setConnectTimeout(CONNECTION_TIMEOUT)
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .build();
            List<NameValuePair> formParams = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, charSet));
            httpPost.setConfig(requestConfig);
            if (CollectionUtils.isNotEmpty(headList)) {
                for (NameValuePair head : headList) {
                    httpPost.addHeader(head.getName(), head.getValue());
                }
            }
            if (printParam) {
                desLogger.info("request url: {}, params: {}", reqURL, params);
            }
            desLogger.info("request url: {}", reqURL);

            response = httpclient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            desLogger.info("request url: {}, statusCode: {}", reqURL, statusCode);

            // 获取响应实体
            entity = response.getEntity();
            if (null != entity) {
                responseContent = EntityUtils.toString(entity, charSet);
            }
            desLogger.info("request url: {}, responseContent: {}", reqURL, responseContent);

            if (statusCode != HttpStatus.SC_OK) {
                throw new RemoteException(
                        String.format("request url: %s, response status error, statusCode: %s", reqURL, statusCode));
            }
        } catch (RemoteException e) {
            throw e;
        } catch (Exception e) {
            desLogger.info(String.format("request url: %s, error params: %s", reqURL, params));
            desLogger.info(String.format("request url: %s, param error", reqURL));
            throw new CommonsException(String.format("request url: %s.", reqURL), e);
        } finally {
            closeConnection(entity, response, httpPost);
            desLogger.info("request url: {} costs {}ms", reqURL, System.currentTimeMillis() - start);
        }
        return responseContent;
    }
}
