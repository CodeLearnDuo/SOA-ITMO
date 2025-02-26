package blps.duo.service;

import blps.duo.model.GetProductsByUnitOfMeasureRequest;
import blps.duo.model.GetProductsByUnitOfMeasureResponse;
import blps.duo.model.IncreasePricesRequest;
import blps.duo.model.IncreasePricesResponse;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.xml.bind.annotation.XmlSeeAlso;

@WebService(targetNamespace = "http://www.example.com/ebay", name = "EbayServicePortType")
@XmlSeeAlso({
        blps.duo.model.ProductList.class,
        blps.duo.model.Product.class,
        blps.duo.model.Coordinates.class,
        blps.duo.model.Organization.class,
        blps.duo.model.UnitOfMeasure.class,
        blps.duo.model.OrganizationType.class,
        blps.duo.model.IncreasePricesResponse.class,
        blps.duo.model.IncreasePricesRequest.class,
        blps.duo.error.Error.class,
        blps.duo.error.EbayApiException.class,
        blps.duo.error.InternalServerErrorException.class,
        blps.duo.error.InvalidPercentageException.class,
        blps.duo.error.InvalidUnitOfMeasureException.class,
        blps.duo.error.ProductsNotFoundException.class,
        blps.duo.error.ServiceUnavailableException.class
})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface EbayService {

    @WebMethod(operationName = "GetProductsByUnitOfMeasure", action = "http://www.example.com/ebay/GetProductsByUnitOfMeasure")
    GetProductsByUnitOfMeasureResponse getProductsByUnitOfMeasure(
            @WebParam(name = "GetProductsByUnitOfMeasureRequest") GetProductsByUnitOfMeasureRequest request);

    @WebMethod(operationName = "IncreasePrices", action = "http://www.example.com/ebay/IncreasePrices")
    IncreasePricesResponse increasePrices(
            @WebParam(name = "IncreasePricesRequest") IncreasePricesRequest request);
}