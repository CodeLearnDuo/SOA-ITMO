package soa.duo.ebayservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import soa.duo.ebayservice.controller.advice.ResourceNotFoundException;
import soa.duo.ebayservice.controller.advice.ServiceUnavailableException;
import soa.duo.ebayservice.model.Product;
import soa.duo.ebayservice.model.dto.ProductInputDto;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductHttpClient.class);
    private static final String BASE_URL = "https://localhost:25643/api/v1/products";
    private static final String TRUSTSTORE_PATH = "/home/studs/s334341/payara/truststore.jks";
    private static final String TRUSTSTORE_PASSWORD = "qwerty";

    private final ObjectMapper objectMapper;

    public ProductHttpClient() {
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public List<Product> getAllProducts() throws Exception {
        try (CloseableHttpClient httpClient = createHttpClient()) {
            HttpGet request = new HttpGet(BASE_URL + "/");
            LOGGER.info("Sending GET request to URL: {}", request.getURI());

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                LOGGER.info("Received response with status: {}", statusCode);

                if (statusCode == 503) {
                    throw new ServiceUnavailableException("Product service is currently unavailable.");
                }
                if (statusCode != 200) {
                    throw new RuntimeException("Failed to retrieve products, status: " + statusCode);
                }

                String responseBody = EntityUtils.toString(response.getEntity());
                LOGGER.info("Response body: {}", responseBody);

                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode productsNode = rootNode.get("content");
                if (productsNode == null || !productsNode.isArray()) {
                    throw new IllegalStateException("Invalid JSON structure: 'content' is missing or not an array");
                }

                List<Product> productList = new ArrayList<>();
                for (JsonNode productNode : productsNode) {
                    Product product = objectMapper.treeToValue(productNode, Product.class);
                    productList.add(product);
                }
                return productList;
            } catch (HttpHostConnectException e) {
                throw new ServiceUnavailableException("Product service is currently unavailable.");
            }
        }
    }

    public boolean patchProduct(Long productId, ProductInputDto productDto) throws Exception {
        try (CloseableHttpClient httpClient = createHttpClient()) {
            String patchUrl = BASE_URL + "/" + productId;
            HttpPatch patchRequest = new HttpPatch(patchUrl);

            String requestBody = objectMapper.writeValueAsString(productDto);
            patchRequest.setEntity(new StringEntity(requestBody, ContentType.APPLICATION_JSON));

            LOGGER.info("Sending PATCH request to URL: {}", patchRequest.getURI());
            LOGGER.info("Request body: {}", requestBody);

            try (CloseableHttpResponse response = httpClient.execute(patchRequest)) {
                int statusCode = response.getStatusLine().getStatusCode();
                LOGGER.info("Received response with status: {}", statusCode);

                if (statusCode == 503) {
                    throw new ServiceUnavailableException("Product service is currently unavailable.");
                }
                if (statusCode == 404) {
                    throw new ResourceNotFoundException("Product with id " + productId + " not found");
                }
                return statusCode == 200;
            } catch (HttpHostConnectException e) {
                throw new ServiceUnavailableException("Product service is currently unavailable.");
            }
        }
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    private SSLContext createSSLContext() throws Exception {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (FileInputStream trustStoreInput = new FileInputStream(TRUSTSTORE_PATH)) {
            trustStore.load(trustStoreInput, TRUSTSTORE_PASSWORD.toCharArray());
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm()
        );
        trustManagerFactory.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
        return sslContext;
    }

    private CloseableHttpClient createHttpClient() throws Exception {
        SSLContext sslContext = createSSLContext();
        SSLConnectionSocketFactory socketFactory =
                new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        return HttpClients.custom()
                .setSSLSocketFactory(socketFactory)
                .build();
    }
}
