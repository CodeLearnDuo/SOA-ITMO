package soa.duo.ebay_service.services;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import soa.duo.ebay_service.dtos.OrganizationInput;
import soa.duo.ebay_service.dtos.ProductInput;
import soa.duo.ebay_service.model.Product;

import java.util.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;

public class PriceUpdateService {

    private static final String BASE_URL = "https://localhost:8443/api/v1/products";
    private static final String TRUSTSTORE_PATH = "C:/Users/Mikhail/Desktop/Service-Oriented-Architecture/Ebay-Service/src/main/resources/truststore.jks";
    private static final String TRUSTSTORE_PASSWORD = "qwerty";

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());


    public String updateAllProductPrices(double percent) throws Exception {
        SSLContext sslContext = createSSLContext();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
                .build();

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
                if (response.getStatusLine().getStatusCode() != 200) {
                    failedUpdates.add(String.valueOf(product.getId()));
                }
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
        SSLContext sslContext = createSSLContext();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(sslContext)
                .build();

        HttpGet request = new HttpGet(BASE_URL + "/");
        List<Product> productList = new ArrayList<>();

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() == 200) {
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
                System.err.println("Failed to retrieve products, status: " + response.getStatusLine().getStatusCode());
            }
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


}
