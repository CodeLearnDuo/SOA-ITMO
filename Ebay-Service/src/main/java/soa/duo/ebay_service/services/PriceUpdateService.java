package soa.duo.ebay_service.services;

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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import soa.duo.ebay_service.dtos.OrganizationInput;
import soa.duo.ebay_service.dtos.ProductInput;
import soa.duo.ebay_service.exception.ServiceUnavailableException;
import soa.duo.ebay_service.model.Product;

import java.util.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;

public class PriceUpdateService {

    private static final String BASE_URL = "https://localhost:25543/api/v1/products";
    private static final String TRUSTSTORE_PATH = "/home/studs/s335156/payara/truststore.jks";
    private static final String TRUSTSTORE_PASSWORD = "qwerty";

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());


    public String updateAllProductPrices(double percent) throws Exception {
        CloseableHttpClient httpClient = createHttpClientWithNoHostVerification();

        List<Product> productList = getAllProducts();
        List<String> failedUpdates = new ArrayList<>();

        for (Product product : productList) {
            double newPrice = product.getPrice() * (1 + percent / 100);

            ProductInput productInput = new ProductInput(
                    product.getName(),
                    product.getCoordinates(),
                    newPrice,
                    product.getPartNumber(),
                    product.getUnitOfMeasure(),
                    new OrganizationInput(
                            product.getManufacturer().getName(),
                            product.getManufacturer().getEmployeesCount(),
                            product.getManufacturer().getType()
                    )
            );

            HttpPatch patchRequest = new HttpPatch(BASE_URL + "/" + product.getId());
            patchRequest.setEntity(new StringEntity(objectMapper.writeValueAsString(productInput), ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = httpClient.execute(patchRequest)) {
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == 503) {
                    throw new ServiceUnavailableException("Product service is currently unavailable.");
                }

                if (statusCode != 200) {
                    failedUpdates.add(String.valueOf(product.getId()));
                }
            } catch (HttpHostConnectException e) {
                throw new ServiceUnavailableException("Product service is currently unavailable.");
            }
        }

        httpClient.close();

        if (failedUpdates.isEmpty()) {
            return "Prices updated successfully";
        } else {
            return "Failed to update products with ids: " + failedUpdates;
        }
    }


    private List<Product> getAllProducts() throws Exception {
        CloseableHttpClient httpClient = createHttpClientWithNoHostVerification();

        HttpGet request = new HttpGet(BASE_URL + "/");
        List<Product> productList = new ArrayList<>();

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 503) {
                throw new ServiceUnavailableException("Product service is currently unavailable.");
            }

            if (statusCode == 200) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("Received JSON from first service: " + responseBody);

                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode productsNode = rootNode.get("content");

                if (productsNode != null && productsNode.isArray()) {
                    for (JsonNode productNode : productsNode) {
                        Product product = objectMapper.treeToValue(productNode, Product.class);
                        productList.add(product);
                    }
                } else {
                    System.err.println("Invalid JSON structure: 'content' is missing or not an array");
                }
            } else {
                System.err.println("Failed to retrieve products, status: " + statusCode);
            }
        } catch (HttpHostConnectException e) {
            throw new ServiceUnavailableException("Product service is currently unavailable.");
        }

        return productList;
    }


    private SSLContext createSSLContext() throws Exception {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (FileInputStream trustStoreInput = new FileInputStream(TRUSTSTORE_PATH)) {
            trustStore.load(trustStoreInput, TRUSTSTORE_PASSWORD.toCharArray());
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

        return sslContext;
    }

    private CloseableHttpClient createHttpClientWithNoHostVerification() throws Exception {
        SSLContext sslContext = createSSLContext();

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
                sslContext,
                NoopHostnameVerifier.INSTANCE
        );

        return HttpClients.custom()
                .setSSLSocketFactory(socketFactory)
                .build();
    }



}
