package blps.duo.error;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFault;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.WebFault;
import jakarta.xml.ws.soap.SOAPFaultException;

import javax.xml.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFault(name = "ApiFault", targetNamespace = "http://www.example.com/ebay", faultBean = "blps.duo.error.Error")
public class SoapExceptionHandler extends SOAPFaultException {

    private static final Logger LOGGER = LoggerFactory.getLogger(SoapExceptionHandler.class);

    public SoapExceptionHandler(String message) throws SOAPException {
        super(createFault(message));
    }

    private static SOAPFault createFault(String message) throws SOAPException {
        SOAPMessage soapMessage = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage();
        SOAPFault fault = soapMessage.getSOAPBody().addFault();
        fault.setFaultCode(new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, "Client"));
        fault.setFaultString(message);
        return fault;
    }

    public static InvalidXmlException handleParsingException(JAXBException e) {
        LOGGER.error("XML parsing error: ", e);
        return new InvalidXmlException("Invalid XML request: " + e.getMessage());
    }
}