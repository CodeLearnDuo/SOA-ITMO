package soa.duo.ebay_service.services;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.http.conn.HttpHostConnectException;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import soa.duo.ebay_service.exception.ServiceUnavailableException;
import soa.duo.ebay_service.model.enums.UnitOfMeasure;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

public class ProductFilterService {

    private static final String BASE_URL = "https://localhost:25543/api/v1/products/";
    private static final String TRUSTSTORE_PATH = "/home/studs/s335156/payara/truststore.jks";
    private static final String TRUSTSTORE_PASSWORD = "qwerty";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String fetchAndFilterProductsByUnitOfMeasure(String unitOfMeasure) throws Exception {

        if (!isValidUnitOfMeasure(unitOfMeasure)) {
            throw new IllegalArgumentException("Invalid unit of measure: " + unitOfMeasure);
        }

        SSLContext sslContext = createSSLContext();

        ClientConfig config = new ClientConfig();
        config.connectorProvider(new ApacheConnectorProvider());

        Client client = ClientBuilder.newBuilder()
                .sslContext(sslContext)
                .hostnameVerifier((hostname, session) -> true)
                .build();

        String responseContent;

        try {
            WebTarget target = client.target(BASE_URL);
            Response response = target.request(MediaType.APPLICATION_JSON).get();

            if (response.getStatus() == 503) {
                throw new ServiceUnavailableException("External service unavailable");
            }

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed to fetch products, status: " + response.getStatus());
            }

            responseContent = response.readEntity(String.class);

            System.out.println("Received JSON from first service: " + responseContent);

            JsonNode productsRoot = objectMapper.readTree(responseContent);
            JsonNode products = productsRoot.get("content");

            if (products == null || !products.isArray()) {
                throw new IllegalStateException("Invalid JSON format: 'content' field is missing or is not an array");
            }

            JsonNode filteredProducts = filterProductsByUnitOfMeasure(products, unitOfMeasure);

            return objectMapper.writeValueAsString(filteredProducts);

        } catch (ProcessingException e) {
            throw new ServiceUnavailableException("Product service is currently unavailable.");
        } finally {
            client.close();
        }
    }

    private boolean isValidUnitOfMeasure(String unitOfMeasure) {
        try {
            UnitOfMeasure.valueOf(unitOfMeasure.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private JsonNode filterProductsByUnitOfMeasure(JsonNode products, String unitOfMeasure) {
        List<JsonNode> filteredProducts = new ArrayList<>();
        for (JsonNode product : products) {
            JsonNode unitNode = product.get("unitOfMeasure");
            if (unitNode != null && unitNode.asText().equalsIgnoreCase(unitOfMeasure)) {
                filteredProducts.add(product);
            }
        }
        return objectMapper.valueToTree(filteredProducts);
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
