package com.yss.acs.mock.webservice.webservice4;

import javax.jws.WebService;


/**
 * generated by mock-platform 
 */
@WebService(targetNamespace = "http://tempuri.org/", serviceName = "UnifiedAuthenSoap", endpointInterface = "com.yss.acs.mock.webservice.webservice4.UnifiedAuthenSoap")
public class UnifiedAuthenSoapMockImpl implements UnifiedAuthenSoap {

    private java.util.Map<String, Object> mockResultMap = new java.util.HashMap<String, Object>();

    public void setMockPlatformResult(java.util.Map<String, Object> mockResultMap) {
        this.mockResultMap = mockResultMap;
    }

    public Object getMockPlatformResult(String methodName) {
        Object obj = mockResultMap.get(methodName);
        obj = com.yss.acs.mock.util.MockResultUtil.getMockResultObject(obj);
        return obj;
    }


    @Override
    public com.yss.acs.mock.webservice.webservice4.GetMessagePWDModel generateMessagePWD(
        java.lang.String authorizedName,
        java.lang.String authorizedKey,
        java.lang.String mobile,
        java.lang.String timeStamp
    ) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return (com.yss.acs.mock.webservice.webservice4.GetMessagePWDModel) getMockPlatformResult(methodName);
    }


    @Override
    public com.yss.acs.mock.webservice.webservice4.AuthenticationModel authenticate(
        java.lang.String authorizeType,
        java.lang.String authorizeName,
        java.lang.String authorizeKey,
        java.lang.String messageKey
    ) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        return (com.yss.acs.mock.webservice.webservice4.AuthenticationModel) getMockPlatformResult(methodName);
    }

}
