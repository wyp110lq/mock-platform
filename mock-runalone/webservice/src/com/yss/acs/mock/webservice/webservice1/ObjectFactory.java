
package com.yss.acs.mock.webservice.webservice1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.yss.acs.mock.webservice.webservice1 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CaseConfirmIn0_QNAME = new QName("http://www.primeton.com/multiTGService", "in0");
    private final static QName _CaseConfirmResponseOut1_QNAME = new QName("http://www.primeton.com/multiTGService", "out1");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.yss.acs.mock.webservice.webservice1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CaseConfirm }
     * 
     */
    public CaseConfirm createCaseConfirm() {
        return new CaseConfirm();
    }

    /**
     * Create an instance of {@link CaseConfirmResponse }
     * 
     */
    public CaseConfirmResponse createCaseConfirmResponse() {
        return new CaseConfirmResponse();
    }

    /**
     * Create an instance of {@link CreateCase }
     * 
     */
    public CreateCase createCreateCase() {
        return new CreateCase();
    }

    /**
     * Create an instance of {@link CreateCaseResponse }
     * 
     */
    public CreateCaseResponse createCreateCaseResponse() {
        return new CreateCaseResponse();
    }

    /**
     * Create an instance of {@link BackoutCase }
     * 
     */
    public BackoutCase createBackoutCase() {
        return new BackoutCase();
    }

    /**
     * Create an instance of {@link BackoutCaseResponse }
     * 
     */
    public BackoutCaseResponse createBackoutCaseResponse() {
        return new BackoutCaseResponse();
    }

    /**
     * Create an instance of {@link QueryCaseStatus }
     * 
     */
    public QueryCaseStatus createQueryCaseStatus() {
        return new QueryCaseStatus();
    }

    /**
     * Create an instance of {@link QueryCaseStatusResponse }
     * 
     */
    public QueryCaseStatusResponse createQueryCaseStatusResponse() {
        return new QueryCaseStatusResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.primeton.com/multiTGService", name = "in0", scope = CaseConfirm.class)
    public JAXBElement<String> createCaseConfirmIn0(String value) {
        return new JAXBElement<String>(_CaseConfirmIn0_QNAME, String.class, CaseConfirm.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.primeton.com/multiTGService", name = "out1", scope = CaseConfirmResponse.class)
    public JAXBElement<String> createCaseConfirmResponseOut1(String value) {
        return new JAXBElement<String>(_CaseConfirmResponseOut1_QNAME, String.class, CaseConfirmResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.primeton.com/multiTGService", name = "in0", scope = CreateCase.class)
    public JAXBElement<String> createCreateCaseIn0(String value) {
        return new JAXBElement<String>(_CaseConfirmIn0_QNAME, String.class, CreateCase.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.primeton.com/multiTGService", name = "in0", scope = BackoutCase.class)
    public JAXBElement<String> createBackoutCaseIn0(String value) {
        return new JAXBElement<String>(_CaseConfirmIn0_QNAME, String.class, BackoutCase.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.primeton.com/multiTGService", name = "out1", scope = QueryCaseStatusResponse.class)
    public JAXBElement<String> createQueryCaseStatusResponseOut1(String value) {
        return new JAXBElement<String>(_CaseConfirmResponseOut1_QNAME, String.class, QueryCaseStatusResponse.class, value);
    }

}
