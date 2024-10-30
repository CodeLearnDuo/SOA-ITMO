package soa.duo.ebay_service.services;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;

public class ProductFilterService {

    private static final String BASE_URL = "https://localhost:8443/api/v1/products/filter/unit-of-measure/";
    private static final String TRUSTSTORE_PATH = "C:/Users/Mikhail/Desktop/Service-Oriented-Architecture/Ebay-Service/src/main/resources/truststore.jks";
    private static final String TRUSTSTORE_PASSWORD = "qwerty";

    public String fetchProductsByUnitOfMeasure(String unitOfMeasure) throws Exception {
        SSLContext sslContext = createSSLContext();

        ClientConfig config = new ClientConfig();
        config.connectorProvider(new ApacheConnectorProvider());

        Client client = ClientBuilder.newBuilder()
                .sslContext(sslContext)
                .hostnameVerifier((hostname, session) -> true)
                .build();

        String responseContent;
        try {
            WebTarget target = client.target(BASE_URL + unitOfMeasure.toUpperCase());
            Response response = target.request(MediaType.APPLICATION_JSON).get();
            responseContent = response.readEntity(String.class);
        } finally {
            client.close();
        }

        return responseContent;
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