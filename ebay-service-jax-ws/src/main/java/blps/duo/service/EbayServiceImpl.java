package blps.duo.service;

import blps.duo.model.GetProductsByUnitOfMeasureRequest;
import blps.duo.model.IncreasePricesRequest;
import blps.duo.model.Product;
import blps.duo.model.ProductList;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.jws.WebService;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@WebService(
        serviceName = "EbayService",
        portName = "EbayServiceSoapPort",
        targetNamespace = "http://www.example.com/ebay",
        endpointInterface = "blps.duo.service.EbayService"
)
public class EbayServiceImpl implements EbayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EbayServiceImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String MULE_GET_URL = "http://localhost:25645/api/v1/muleadapter/products";
    private static final String MULE_PATCH_BASE_URL = "http://localhost:25645/api/v1/muleadapter/product/";

    @Override
    public Object getProductsByUnitOfMeasure(GetProductsByUnitOfMeasureRequest request) {
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
            List<Product> listOfProducts = objectMapper.readValue(responseContent.toString(), new TypeReference<List<Product>>() {});
            ProductList productList = new ProductList();
            productList.setProduct(listOfProducts);

            LOGGER.info("Returning response with {} products", listOfProducts.size());
            return productList;  // Возвращаемый объект будет обёрнут JAX-WS
        } catch (Exception e) {
            LOGGER.error("Exception in getProductsByUnitOfMeasure: ", e);
            throw new RuntimeException("Exception in getProductsByUnitOfMeasure: " + e.getMessage(), e);
        }
    }

    @Override
    public Object increasePrices(IncreasePricesRequest request) {
        System.out.println("DEBUG: In increasePrices");
        try {
            double percent = request.getIncreasePercent();
            if (percent < 0) {
                LOGGER.error("Invalid percentage value (cannot be negative): {}", percent);
                throw new IllegalArgumentException("Invalid percentage value (cannot be negative)");
            }
            LOGGER.info("Starting price increase process with percent: {}", percent);

            URL url = new URL(MULE_GET_URL);
            HttpURLConnection getConn = (HttpURLConnection) url.openConnection();
            getConn.setRequestMethod("GET");
            getConn.setRequestProperty("Accept", "application/json");

            int getCode = getConn.getResponseCode();
            LOGGER.info("GET integration response code for fetching products: {}", getCode);
            if (getCode != HttpURLConnection.HTTP_OK) {
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

            List<Product> listOfProducts = objectMapper.readValue(responseContent.toString(), new TypeReference<List<Product>>() {});
            LOGGER.info("Fetched {} products for price update", listOfProducts.size());

            for (Product product : listOfProducts) {
                String patchUrl = MULE_PATCH_BASE_URL + product.getId() + "?increasePercent=" + percent;
                LOGGER.info("Calling Mule PATCH integration flow for product id {} at: {}", product.getId(), patchUrl);
                URL patchURL = new URL(patchUrl);
                HttpURLConnection patchConn = (HttpURLConnection) patchURL.openConnection();
                patchConn.setRequestMethod("PATCH");
                patchConn.setRequestProperty("Content-Type", "application/json");
                patchConn.setDoOutput(false);

                int patchCode = patchConn.getResponseCode();
                LOGGER.info("Mule PATCH integration response code for product {}: {}", product.getId(), patchCode);
                if (patchCode != HttpURLConnection.HTTP_NO_CONTENT) {
                    StringBuilder errorResponse = new StringBuilder();
                    try (BufferedReader errorIn = new BufferedReader(new InputStreamReader(patchConn.getErrorStream()))) {
                        String line;
                        while ((line = errorIn.readLine()) != null) {
                            errorResponse.append(line);
                        }
                    }
                    throw new RuntimeException("Error updating product id " + product.getId() + ", response code: " + patchCode
                            + ", error: " + errorResponse);
                }
                patchConn.disconnect();
            }

            LOGGER.info("Price increase process completed successfully.");
            return new Object(); // Возвращаем пустой объект, так как JAX-WS создаст `IncreasePricesResponse`
        } catch (Exception e) {
            LOGGER.error("Exception in increasePrices: ", e);
            throw new RuntimeException("Exception in increasePrices: " + e.getMessage(), e);
        }
    }
}