package blps.duo.service;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import blps.duo.model.GetProductsByUnitOfMeasureRequest;
import blps.duo.model.IncreasePricesRequest;

@WebService(targetNamespace = "http://www.example.com/ebay", name = "EbayServicePortType")
public interface EbayService {

    @WebMethod(operationName = "GetProductsByUnitOfMeasure")
    @WebResult(name = "GetProductsByUnitOfMeasureResponse") // JAX-WS сам создаст класс
    Object getProductsByUnitOfMeasure(@WebParam(name = "GetProductsByUnitOfMeasureRequest") GetProductsByUnitOfMeasureRequest request);

    @WebMethod(operationName = "IncreasePrices")
    @WebResult(name = "IncreasePricesResponse") // JAX-WS сам создаст класс
    Object increasePrices(@WebParam(name = "IncreasePricesRequest") IncreasePricesRequest request);
}