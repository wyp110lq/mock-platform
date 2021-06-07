package com.yss.acs.mock.server.common.constants;

/**
 * 常量配置
 *
 * @author jiayy
 * @date 2020/6/28
 */
public class Constants {

    public static final String URI_ROOT_PATH = "/";

    public static final String CONTENT_TYPE_JSON = "application/json;charset=utf-8";
    public static final String CONTENT_TYPE_XML = "application/xml;charset=utf-8";
    public static final String CONTENT_TYPE_TEXT = "text/html;charset=utf-8";

    public static final String REQ_CONTENT_TYPE_FORMDATA = "form-data";
    public static final String REQ_CONTENT_TYPE_JSON = "application/json";
    public static final String REQ_CONTENT_TYPE_XML = "application/xml";
    public static final String REQ_CONTENT_TYPE_TEXT = "text/plain";

    public static final int MAX_SERVICE_TIME = 60;

    public static final String LOCALHOST_IP = "127.0.0.1";

    public static final String WEBSERVICE_URI_PREFIX = "/acsmock/webservice";

    public static final String HESSIAN_URI_PREFIX = "/acsmock/hessian";

    public static final String STRING_POINT = ".";

    public static final String STRING_QUESTION = "\\?";

    public static final String STRING_SPLIT_POINT = "\\.";

    public static final String URI_WSDL_END = "?wsdl";

    public static final String URI_WSDL_PARAM = "wsdl";

    public static final String CACHE_URI_KEY = "uri";

    public static final String FILE_PACKAGE_INFO = "package-info.class";

    public static final String WSCODE_PACKAGE_PREFIX = "webservice";

    public static final String HSCODE_PACKAGE_PREFIX = "hessian";

    public static final String CLASS_EXT_NAME = ".class";

    public static final String JAVA_PACKAGE_BEGIN = "package ";

    public static final String JAVA_WS_ANNOTATION = "@WebService(";

    public static final String JAVA_INTERFACE_KEYWORD = " interface ";

    public static final String JAVA_BODY_BEGIN = "{";

    public static final String JAVA_FUNCTION_VOID = " void ";


    public static final String JAVA_EXT_NAME = ".java";

    public static final String JAVA_IMPL_NAME = "MockImpl";

    public static final String JAVA_IMPORT_BEGIN = "import ";

    public static final String JAVA_WS_NAME = ", name = ";

    public static final String JAVA_WS_SERVICENAME = ", serviceName = ";

    public static final String JAVA_WS_ENDPOINT = ", endpointInterface = \"";

    public static final String JAVA_DOC_STAR = "*";

    public static final String JAVA_INTERFACE_BEGIN = "public interface ";

    public static final String JAVA_CLASS_BEGIN = "public class ";

    public static final String JAVA_WEBSERVICE_PACKAGE = "import javax.jws.WebService;";

    public static final String JAVA_FILE_NOTE = "/**\r\n * generated by mock-platform \r\n */";

    public static final String JAVA_IMPLEMENTS_NAME = " implements ";

    public static final String JAVA_WEB_METHOD = "@WebMethod";

    public static final String JAVA_ANNOTATION_BEGIN = "@";

    public static final String JAVA_OVERRIDE = "    @Override";

    public static final String JAVA_METHOD_PUBLIC = "public";

    public static final String JAVA_METHOD_BEGIN = "(";

    public static final String JAVA_FUNCTION_END = ";";

    public static final String JAVA_END_STRING = ";";

    public static final String JAVA_RETURN_VOID = " {}";

    public static final String JAVA_MOCK_RESULT_PROPERTY =
            "    private java.util.Map<String, Object> mockResultMap = new java.util.HashMap<String, Object>();\n";

    public static final String JAVA_MOCK_RESULT_SET_METHOD =
            "    public void setMockPlatformResult(java.util.Map<String, Object> mockResultMap) {\n" +
            "        this.mockResultMap = mockResultMap;\n" +
            "    }\n";

    public static final String JAVA_MOCK_RESULT_GET_METHOD = 
            "    public Object getMockPlatformResult(String methodName) {\n" +
            "        Object obj = mockResultMap.get(methodName);\n" +
            "        obj = com.yss.acs.mock.util.MockResultUtil.getMockResultObject(obj);\n" +
            "        return obj;\n" +
            "    }\n";

    public static final String JAVA_RETURN_OBJECT = " {\n" +
            "        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();\n" +
            "        return (%s) getMockPlatformResult(methodName);\n" +
            "    }\n";

    public static final String JAVA_ENTER_STRING = "\n";

    public static final String METHOD_SET_MOCK_RESULT = "setMockPlatformResult";

    public static final String METHOD_GET_MOCK_RESULT = "getMockPlatformResult";

}
