package blps.duo.service;

import blps.duo.model.GetProductsByUnitOfMeasureRequest;
import blps.duo.model.IncreasePricesRequest;
import blps.duo.model.IncreasePricesResponse;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlSeeAlso;

@WebService(targetNamespace = "http://www.example.com/ebay", name = "EbayServicePortType")
@XmlSeeAlso({blps.duo.model.ProductList.class, blps.duo.model.Product.class,
        blps.duo.model.Coordinates.class, blps.duo.model.Organization.class,
        blps.duo.model.UnitOfMeasure.class, blps.duo.model.OrganizationType.class,
        IncreasePricesResponse.class, IncreasePricesRequest.class})
public interface EbayService {

    @WebMethod(operationName = "GetProductsByUnitOfMeasure",action = "http://www.example.com/ebay/GetProductsByUnitOfMeasure")
    @WebResult(name = "GetProductsByUnitOfMeasureResponse") // JAX-WS сам создаст класс
    Object getProductsByUnitOfMeasure(@WebParam(name = "GetProductsByUnitOfMeasureRequest") GetProductsByUnitOfMeasureRequest request);

    @WebMethod(operationName = "IncreasePrices", action = "http://www.example.com/ebay/IncreasePrices")
    @WebResult(name = "CustomIncreasePricesResponse") // JAX-WS сам создаст класс
    Object increasePrices(@WebParam(name = "IncreasePricesRequest") IncreasePricesRequest request);
}