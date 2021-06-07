
package com.yss.acs.mock.webservice.webservice4;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.yss.acs.mock.webservice.webservice4 package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.yss.acs.mock.webservice.webservice4
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GenerateMessagePWD }
     * 
     */
    public GenerateMessagePWD createGenerateMessagePWD() {
        return new GenerateMessagePWD();
    }

    /**
     * Create an instance of {@link GenerateMessagePWDResponse }
     * 
     */
    public GenerateMessagePWDResponse createGenerateMessagePWDResponse() {
        return new GenerateMessagePWDResponse();
    }

    /**
     * Create an instance of {@link GetMessagePWDModel }
     * 
     */
    public GetMessagePWDModel createGetMessagePWDModel() {
        return new GetMessagePWDModel();
    }

    /**
     * Create an instance of {@link Authenticate }
     * 
     */
    public Authenticate createAuthenticate() {
        return new Authenticate();
    }

    /**
     * Create an instance of {@link AuthenticateResponse }
     * 
     */
    public AuthenticateResponse createAuthenticateResponse() {
        return new AuthenticateResponse();
    }

    /**
     * Create an instance of {@link AuthenticationModel }
     * 
     */
    public AuthenticationModel createAuthenticationModel() {
        return new AuthenticationModel();
    }

}
