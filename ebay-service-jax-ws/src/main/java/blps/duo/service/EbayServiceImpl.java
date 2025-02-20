package blps.duo.service;

import blps.duo.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebService(
        serviceName = "EbayService",
        portName = "EbayServiceSoapPort",
        targetNamespace = "http://www.example.com/ebay",
        endpointInterface = "blps.duo.service.EbayService"
)
@XmlSeeAlso({blps.duo.model.ProductList.class, blps.duo.model.Product.class,
        blps.duo.model.Coordinates.class, blps.duo.model.Organization.class,
        blps.duo.model.UnitOfMeasure.class, blps.duo.model.OrganizationType.class,
        IncreasePricesResponse.class, IncreasePricesRequest.class})
public class EbayServiceImpl implements EbayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EbayServiceImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String MULE_GET_URL = "http://localhost:25645/api/v1/muleadapter/products";
    private static final String MULE_POST_BASE_URL = "http://localhost:25645/api/v1/muleadapter/product/";

    @Override
    public ProductList getProductsByUnitOfMeasure(GetProductsByUnitOfMeasureRequest request) {
        System.out.println("DEBUG: In getProductsByUnitOfMeasure");
        try {
            String unit = request.getUnitOfMeasure().toString();
            String targetUrl = MULE_GET_URL;
            if (unit != null && !unit.trim().isEmpty()) {
                targetUrl += "?unitOfMeasure=" + unit;
            }
            LOGGER.info("Calling Mule GET integration flow at: {}", targetUrl);

            URL url = new URL(targetUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            int code = conn.getResponseCode();
            LOGGER.info("Mule GET integration response code: {}", code);
            if (code != HttpURLConnection.HTTP_OK) {
                LOGGER.error("Error calling Mule GET integration flow, response code: {}", code);
                throw new RuntimeException("Error calling Mule GET integration flow, response code: " + code);
            }

            StringBuilder responseContent = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    responseContent.append(line);
                }
            }
            conn.disconnect();

            LOGGER.debug("Mule GET integration raw response: {}", responseContent);
            JsonNode rootNode = objectMapper.readTree(responseContent.toString());
            JsonNode contentNode = rootNode.get("content");
            List<Product> listOfProducts = objectMapper.readValue(contentNode.toString(), new TypeReference<List<Product>>() {
            });
            ProductList productList = new ProductList();
            productList.setProducts(listOfProducts);

            LOGGER.info("Returning response with {} products", listOfProducts.size());
            return productList;  // Возвращаемый объект будет обёрнут JAX-WS
        } catch (Exception e) {
            LOGGER.error("Exception in getProductsByUnitOfMeasure: ", e);
            throw new RuntimeException("Exception in getProductsByUnitOfMeasure: " + e.getMessage(), e);
        }
    }

    @Override
    public IncreasePricesResponse increasePrices(IncreasePricesRequest request) {
        System.out.println("DEBUG: In increasePrices");
        try {
            double percent = request.getIncreasePercent();
            if (percent < 0) {
                LOGGER.error("Invalid percentage value (cannot be negative): {}", percent);
                throw new IllegalArgumentException("Invalid percentage value (cannot be negative)");
            }
            LOGGER.info("Starting price increase process with percent: {}", percent);

            // Выполняем GET-запрос к интеграционному потоку Mule для получения списка продуктов
            URL url = new URL(MULE_GET_URL);
            HttpURLConnection getConn = (HttpURLConnection) url.openConnection();
            getConn.setRequestMethod("GET");
            getConn.setRequestProperty("Accept", "application/json");

            int getCode = getConn.getResponseCode();
            LOGGER.info("GET integration response code for fetching products: {}", getCode);
            if (getCode != HttpURLConnection.HTTP_OK) {
                LOGGER.error("Error calling Mule GET (all) integration flow, response code: {}", getCode);
                throw new RuntimeException("Error fetching products, response code: " + getCode);
            }

            StringBuilder responseContent = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(getConn.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    responseContent.append(line);
                }
            }
            getConn.disconnect();

            LOGGER.debug("GET integration raw response: {}", responseContent);

            JsonNode rootNode = objectMapper.readTree(responseContent.toString());
            JsonNode contentNode = rootNode.get("content");
            List<Product> listOfProducts = objectMapper.readValue(contentNode.toString(), new TypeReference<List<Product>>() {
            });
            LOGGER.info("Fetched {} products for price update", listOfProducts.size());

            // Для каждого продукта вызываем Mule POST-интеграционный поток для обновления цены.
            // Важно: здесь мы вызываем Mule через POST, а Mule flow внутри вызывает PATCH к Product API.
            for (Product product : listOfProducts) {
// Для каждого продукта вызываем Mule POST-интеграционный поток для обновления цены.
                LOGGER.info("Start updating price for product: {}", product);
                Double oldPrice = product.getPrice();
                if (oldPrice == null) {
                    LOGGER.info("Skipping product with id {} since price is null.", product.getId());
                    continue;
                    }
                double newPrice = oldPrice * (1 + percent / 100);
                LOGGER.info("Product id {} old price: {} => new price: {}", product.getId(), oldPrice, newPrice);

                product.setPrice(newPrice);
                // Вызываем Mule POST-интеграционный поток для обновления цены
                URL postUrl = new URL(MULE_POST_BASE_URL + product.getId());
                HttpURLConnection postConn = (HttpURLConnection) postUrl.openConnection();
                postConn.setRequestMethod("POST");
                postConn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                postConn.setDoOutput(true);
                String jsonPayload = objectMapper.writeValueAsString(product);
                LOGGER.info("POST payload for product id {}: {}", product.getId(), jsonPayload);

                try (OutputStream os = postConn.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int postCode = postConn.getResponseCode();
                if (postCode != HttpURLConnection.HTTP_OK && postCode != HttpURLConnection.HTTP_CREATED) {
                    LOGGER.error("Failed to update product with id {} via POST, response code: {}", product.getId(), postCode);
                    throw new RuntimeException("Failed to update product with id " + product.getId() + ", response code: " + postCode);
                } else {
                    LOGGER.info("Successfully updated product with id {} via POST, response code: {}", product.getId(), postCode);
                }
                postConn.disconnect();
            }
            LOGGER.info("Price increase process completed successfully.");
            return new IncreasePricesResponse();
        } catch (Exception e) {
            LOGGER.error("Exception in increasePrices: ", e);
            throw new RuntimeException("Exception in increasePrices: " + e.getMessage(), e);
        }
    }
}