
package com.yss.acs.mock.webservice.webservice7;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.yss.acs.mock.webservice.webservice7 package. 
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

    private final static QName _FindBondFromACS_QNAME = new QName("http://webservice.publishservice.api.acs.yss.com/", "findBondFromACS");
    private final static QName _FindBondFromACSResponse_QNAME = new QName("http://webservice.publishservice.api.acs.yss.com/", "findBondFromACSResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.yss.acs.mock.webservice.webservice7
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FindBondFromACS }
     * 
     */
    public FindBondFromACS createFindBondFromACS() {
        return new FindBondFromACS();
    }

    /**
     * Create an instance of {@link FindBondFromACSResponse }
     * 
     */
    public FindBondFromACSResponse createFindBondFromACSResponse() {
        return new FindBondFromACSResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindBondFromACS }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.publishservice.api.acs.yss.com/", name = "findBondFromACS")
    public JAXBElement<FindBondFromACS> createFindBondFromACS(FindBondFromACS value) {
        return new JAXBElement<FindBondFromACS>(_FindBondFromACS_QNAME, FindBondFromACS.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FindBondFromACSResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.publishservice.api.acs.yss.com/", name = "findBondFromACSResponse")
    public JAXBElement<FindBondFromACSResponse> createFindBondFromACSResponse(FindBondFromACSResponse value) {
        return new JAXBElement<FindBondFromACSResponse>(_FindBondFromACSResponse_QNAME, FindBondFromACSResponse.class, null, value);
    }

}
