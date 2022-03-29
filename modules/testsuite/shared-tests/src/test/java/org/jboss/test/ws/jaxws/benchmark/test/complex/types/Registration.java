
package org.jboss.test.ws.jaxws.benchmark.test.complex.types;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.3-b02-
 * Generated source version: 2.1
 * 
 */
@WebService(name = "Registration", targetNamespace = "http://types.complex.jaxws.ws.test.jboss.org/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface Registration {


    /**
     * 
     * @param when
     * @param customers
     * @return
     *     returns java.util.List<java.lang.Long>
     * @throws AlreadyRegisteredFault_Exception
     * @throws ValidationFault_Exception
     */
    @WebMethod(operationName = "BulkRegister")
    @WebResult(name = "RegisteredIDs", targetNamespace = "http://types.complex.jaxws.ws.test.jboss.org/")
    @RequestWrapper(localName = "BulkRegister", targetNamespace = "http://types.complex.jaxws.ws.test.jboss.org/", className = "org.jboss.test.ws.jaxws.benchmark.test.complex.types.BulkRegister")
    @ResponseWrapper(localName = "BulkRegisterResponse", targetNamespace = "http://types.complex.jaxws.ws.test.jboss.org/", className = "org.jboss.test.ws.jaxws.benchmark.test.complex.types.BulkRegisterResponse")
    public List<Long> bulkRegister(
        @WebParam(name = "Customers", targetNamespace = "http://types.complex.jaxws.ws.test.jboss.org/")
        List<Customer> customers,
        @WebParam(name = "When", targetNamespace = "http://types.complex.jaxws.ws.test.jboss.org/")
        Object when)
        throws AlreadyRegisteredFault_Exception, ValidationFault_Exception
    ;

    /**
     * 
     * @param customer
     * @return
     *     returns org.jboss.test.ws.jaxws.benchmark.test.complex.types.Statistics
     */
    @WebMethod(operationName = "GetStatistics")
    @WebResult(name = "Statistics", targetNamespace = "http://types.complex.jaxws.ws.test.jboss.org/")
    @RequestWrapper(localName = "GetStatistics", targetNamespace = "http://types.complex.jaxws.ws.test.jboss.org/", className = "org.jboss.test.ws.jaxws.benchmark.test.complex.types.GetStatistics")
    @ResponseWrapper(localName = "GetStatisticsResponse", targetNamespace = "http://types.complex.jaxws.ws.test.jboss.org/", className = "org.jboss.test.ws.jaxws.benchmark.test.complex.types.GetStatisticsResponse")
    public Statistics getStatistics(
        @WebParam(name = "Customer", targetNamespace = "http://types.complex.jaxws.ws.test.jboss.org/")
        Customer customer);

    /**
     * 
     * @param when
     * @param customer
     * @return
     *     returns long
     * @throws AlreadyRegisteredFault_Exception
     * @throws ValidationFault_Exception
     */
    @WebMethod(operationName = "Register")
    @WebResult(name = "RegisteredID", targetNamespace = "http://types.complex.jaxws.ws.test.jboss.org/")
    @RequestWrapper(localName = "Register", targetNamespace = "http://types.complex.jaxws.ws.test.jboss.org/", className = "org.jboss.test.ws.jaxws.benchmark.test.complex.types.Register")
    @ResponseWrapper(localName = "RegisterResponse", targetNamespace = "http://types.complex.jaxws.ws.test.jboss.org/", className = "org.jboss.test.ws.jaxws.benchmark.test.complex.types.RegisterResponse")
    public long register(
        @WebParam(name = "Customer", targetNamespace = "http://types.complex.jaxws.ws.test.jboss.org/")
        Customer customer,
        @WebParam(name = "When", targetNamespace = "http://types.complex.jaxws.ws.test.jboss.org/")
        Object when)
        throws AlreadyRegisteredFault_Exception, ValidationFault_Exception
    ;

    /**
     * 
     * @param invoiceCustomer
     * @return
     *     returns boolean
     * @throws AlreadyRegisteredFault_Exception
     * @throws ValidationFault_Exception
     */
    @WebMethod(operationName = "RegisterForInvoice")
    @WebResult(name = "done", targetNamespace = "http://types.complex.jaxws.ws.test.jboss.org/")
    @RequestWrapper(localName = "RegisterForInvoice", targetNamespace = "http://types.complex.jaxws.ws.test.jboss.org/", className = "org.jboss.test.ws.jaxws.benchmark.test.complex.types.RegisterForInvoice")
    @ResponseWrapper(localName = "RegisterForInvoiceResponse", targetNamespace = "http://types.complex.jaxws.ws.test.jboss.org/", className = "org.jboss.test.ws.jaxws.benchmark.test.complex.types.RegisterForInvoiceResponse")
    public boolean registerForInvoice(
        @WebParam(name = "InvoiceCustomer", targetNamespace = "http://types.complex.jaxws.ws.test.jboss.org/")
        InvoiceCustomer invoiceCustomer)
        throws AlreadyRegisteredFault_Exception, ValidationFault_Exception
    ;

}
