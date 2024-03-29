package com.yss.acs.mock.webservice.webservice2;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 3.2.14
 * 2020-09-17T17:11:59.116+08:00
 * Generated source version: 3.2.14
 *
 */
@WebService(targetNamespace = "http://www.primeton.com/multiTGService", name = "multiTGService")
@XmlSeeAlso({ObjectFactory.class})
public interface MultiTGService {

    @WebMethod
    @RequestWrapper(localName = "caseConfirm", targetNamespace = "http://www.primeton.com/multiTGService", className = "com.yss.acs.mock.webservice.webservice2.CaseConfirm")
    @ResponseWrapper(localName = "caseConfirmResponse", targetNamespace = "http://www.primeton.com/multiTGService", className = "com.yss.acs.mock.webservice.webservice2.CaseConfirmResponse")
    @WebResult(name = "out1", targetNamespace = "http://www.primeton.com/multiTGService")
    public java.lang.String caseConfirm(
        @WebParam(name = "in0", targetNamespace = "http://www.primeton.com/multiTGService")
        java.lang.String in0
    );

    @WebMethod
    @RequestWrapper(localName = "createCase", targetNamespace = "http://www.primeton.com/multiTGService", className = "com.yss.acs.mock.webservice.webservice2.CreateCase")
    @ResponseWrapper(localName = "createCaseResponse", targetNamespace = "http://www.primeton.com/multiTGService", className = "com.yss.acs.mock.webservice.webservice2.CreateCaseResponse")
    @WebResult(name = "out1", targetNamespace = "http://www.primeton.com/multiTGService")
    public java.lang.String createCase(
        @WebParam(name = "in0", targetNamespace = "http://www.primeton.com/multiTGService")
        java.lang.String in0
    );

    @WebMethod
    @RequestWrapper(localName = "queryCaseStatus", targetNamespace = "http://www.primeton.com/multiTGService", className = "com.yss.acs.mock.webservice.webservice2.QueryCaseStatus")
    @ResponseWrapper(localName = "queryCaseStatusResponse", targetNamespace = "http://www.primeton.com/multiTGService", className = "com.yss.acs.mock.webservice.webservice2.QueryCaseStatusResponse")
    @WebResult(name = "out1", targetNamespace = "http://www.primeton.com/multiTGService")
    public java.lang.String queryCaseStatus(
        @WebParam(name = "in0", targetNamespace = "http://www.primeton.com/multiTGService")
        java.lang.String in0
    );

    @WebMethod
    @RequestWrapper(localName = "backoutCase", targetNamespace = "http://www.primeton.com/multiTGService", className = "com.yss.acs.mock.webservice.webservice2.BackoutCase")
    @ResponseWrapper(localName = "backoutCaseResponse", targetNamespace = "http://www.primeton.com/multiTGService", className = "com.yss.acs.mock.webservice.webservice2.BackoutCaseResponse")
    @WebResult(name = "out1", targetNamespace = "http://www.primeton.com/multiTGService")
    public java.lang.String backoutCase(
        @WebParam(name = "in0", targetNamespace = "http://www.primeton.com/multiTGService")
        java.lang.String in0
    );
}
