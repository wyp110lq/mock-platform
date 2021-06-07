package com.yss.acs.mock.server.service.impl;

import com.yss.acs.mock.server.common.config.ServerConfig;
import com.yss.acs.mock.server.common.constants.Constants;
import com.yss.acs.mock.server.common.exception.MockException;
import com.yss.acs.mock.server.common.utils.FileUtil;
import com.yss.acs.mock.server.common.utils.JsonUtil;
import com.yss.acs.mock.server.common.utils.OSinfo;
import com.yss.acs.mock.server.service.IMockService;
import com.yss.acs.mock.server.service.IWebService;
import lombok.extern.slf4j.Slf4j;
import com.yss.acs.mock.server.common.classloader.MockClassLoader;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * WebService服务管理
 *
 * @author jiayy
 * @date 2020/7/3
 */
@Service
@Slf4j
public class WebServiceImpl implements IWebService {

    @Value("${mock.webservice.srcpath}")
    private String wsSrcPath;

    @Value("${mock.webservice.classpath}")
    private String wsClassPath;

    @Value("${mock.webservice.package}")
    private String wsPackage;

    @Value("${mock.webservice.javascript}")
    private String wsJavaScript;

    @Value("${mock.webservice.classscript}")
    private String wsClassScript;

    @Autowired
    private Bus bus;

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private IMockService mockService;

    @Autowired
    private MockClassLoader mockClassLoader;

    private static Map<String, EndpointImpl> webserviceMap = new ConcurrentHashMap<>();

    @Override
    public void validConfig() {
        if (StringUtils.isEmpty(wsSrcPath)) {
            throw new MockException("未配置WebService代码生成路径");
        }
        if (StringUtils.isEmpty(wsClassPath)) {
            throw new MockException("未配置WebService编译代码生成路径");
        }
        if (StringUtils.isEmpty(wsPackage)) {
            throw new MockException("未配置WebService代码包名");
        }
        if (StringUtils.isEmpty(wsJavaScript)) {
            throw new MockException("未配置CXF脚本路径");
        }
        if (StringUtils.isEmpty(wsClassScript)) {
            throw new MockException("未配置加载脚本路径");
        }
        File file = new File(wsSrcPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(wsClassPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(wsJavaScript);
        if (!file.exists()) {
            log.error("WebServiceCXF脚本文件不存在, filePath:{}", wsJavaScript);
            throw new MockException("WebServiceCXF脚本文件不存在");
        }
        file = new File(wsClassScript);
        if (!file.exists()) {
            log.error("WebService代码加载脚本文件不存在, filePath:{}", wsClassScript);
            throw new MockException("WebService代码加载脚本文件不存在");
        }
    }

    @Override
    public String generate(int serviceNumber, String wsdlUrl) {
        String codeSrcPath = getCodeSrcRootPath();
        String codePackage = getCodePackage(serviceNumber);

        log.info("WebService代码生成开始, packageName:{}, wsdlUrl:{}", codePackage, wsdlUrl);
        String fullWsdlUrl = serverConfig.getUrl() + wsdlUrl;

        //代码生成
        String interfaceImplClass = callCodeCreate(codeSrcPath, codePackage, fullWsdlUrl);
        //代码编译
        callCodeCompile(codePackage);

        //接口实现类class完整路径
        String interfaceImplClassPath = wsClassPath + File.separator + interfaceImplClass.replaceAll(Constants.STRING_SPLIT_POINT, "\\" + File.separator) + Constants.CLASS_EXT_NAME;
        //检测是否编译完成
        boolean isCompiled = isCompiled(interfaceImplClassPath);
        if (!isCompiled) {
            log.error("WebService编译代码出现异常，未找到接口文件, 文件路径:{}", interfaceImplClassPath);
            throw new MockException("WebService服务发布失败，未找到接口文件");
        }

        String codePath = codePackage.replaceAll(Constants.STRING_SPLIT_POINT, "\\" + File.separator);
        String classpath = wsClassPath + File.separator + codePath;

        log.info("热加载路径下的class文件, classpath:{}", classpath);
        mockClassLoader.loadClassPath(classpath, codePackage);

        return interfaceImplClass;
    }

    @Override
    public String publish(int serviceNumber, String uri, String className, boolean isNewService, String resResult) {
        log.info("WebService服务发布开始, uri:{}, className:{}", uri, className);
        try {
            //加载class
            reloadClass(serviceNumber);

            Class clazz = mockClassLoader.getClass(className);
            if (clazz == null) {
                log.error("WebService服务发布失败, 未找到接口class, uri:{}, className:{}", uri, className);
                throw new MockException("WebService服务发布失败");
            }
            Object obj = clazz.newInstance();

            Map<String, Object> mockResult;
            if (isNewService) {
                mockResult = mockService.getDefaultMockResult(clazz.getInterfaces()[0]);
            } else {
                mockResult = mockService.getMockConfigResult(clazz.getInterfaces()[0], resResult);
            }
            //设置mock结果
            setMockResult(obj, mockResult);

            String wsdlUri = uri.split(Constants.STRING_QUESTION)[0];
            //先停止
            stop(wsdlUri);

            //发布服务
            EndpointImpl endpoint = new EndpointImpl(bus, obj);
            endpoint.publish(wsdlUri);

            webserviceMap.put(wsdlUri, endpoint);

            log.info("WebService服务发布成功, uri:{}, className:{}", uri, className);
            String mockResResult = "";
            if (isNewService) {
                mockResResult = JsonUtil.objToJson(mockResult);
            }
            return mockResResult;
        } catch (MockException e) {
            throw e;
        } catch (Exception e) {
            log.error("WebService服务发布失败, 创建代理实现类失败, uri:{}, className:{}", uri, className, e);
            throw new MockException("WebService服务发布失败");
        }
    }

    @Override
    public void stop(String uri) {
        try {
            uri = uri.split(Constants.STRING_QUESTION)[0];
            if (webserviceMap.containsKey(uri)) {
                webserviceMap.get(uri).stop();
                webserviceMap.remove(uri);
                log.info("停止WebService服务, uri:{}", uri);
            }
        } catch (Exception e) {
            log.error("停止WebService服务异常, uri:{}", uri, e);
            throw new MockException("停止WebService服务失败");
        }
    }

    /**
     * 校验代码是否编译完成
     *
     * @param implClassPath 实现类class路径
     * @return
     */
    private boolean isCompiled(String implClassPath) {
        File file = new File(implClassPath);
        return file.exists();
    }

    @Override
    public void deleteCode(int serviceNumber) {
        String codePackage = getCodePackage(serviceNumber);
        String codeSrcFullPath = getCodeSrcFullPath(codePackage);
        String codeClassFullPath = getCodeClassFullPath(codePackage);

        FileUtil.deleteDir(codeSrcFullPath);
        FileUtil.deleteDir(codeClassFullPath);
    }

    /**
     * 获取代码包名
     *
     * @param serviceNumber
     * @return
     */
    private String getCodePackage(int serviceNumber) {
        return wsPackage + Constants.STRING_POINT + Constants.WSCODE_PACKAGE_PREFIX + serviceNumber;
    }

    /**
     * 获取源码跟路径
     *
     * @return
     */
    private String getCodeSrcRootPath() {
        return wsSrcPath;
    }

    /**
     * 获取src代码完整路径
     *
     * @param codePackage
     * @return
     */
    private String getCodeSrcFullPath(String codePackage) {
        return wsSrcPath + File.separator + codePackage.replaceAll(Constants.STRING_SPLIT_POINT, "\\" + File.separator);
    }

    /**
     * 获取class代码完整路径
     *
     * @param codePackage
     * @return
     */
    private String getCodeClassFullPath(String codePackage) {
        return wsClassPath + File.separator + codePackage.replaceAll(Constants.STRING_SPLIT_POINT, "\\" + File.separator);
    }

    /**
     * 执行代码生成命令，返回接口实现类包路径
     *
     * @param codeSrcPath
     * @param codePackage
     * @param wsdlUrl
     */
    private String callCodeCreate(String codeSrcPath, String codePackage, String wsdlUrl) {
        String cmd = null;
        if (OSinfo.isWindows()) {
            cmd = String.format("cmd /c start %s %s %s %s", wsJavaScript, codeSrcPath, codePackage, wsdlUrl);
        } else if (OSinfo.isLinux()) {
            cmd = String.format("%s %s %s %s", wsJavaScript, codeSrcPath, codePackage, wsdlUrl);
        }

        log.info("开始执行代码生成脚本, 执行命令:{}", cmd);
        execCommand(cmd);

        String interfaceImplClass = analysisWsJavaFilePath(codeSrcPath, codePackage);
        log.info("WebService代码生成完成, packageName:{}, wsdlUrl:{}", codePackage, wsdlUrl);

        return interfaceImplClass;
    }

    /**
     * 编译class
     *
     * @param codePackage
     */
    private void callCodeCompile(String codePackage) {
        String codePath = codePackage.replaceAll(Constants.STRING_SPLIT_POINT, "\\" + File.separator);
        String cmd = null;
        if (OSinfo.isWindows()) {
            cmd = String.format("cmd /c start %s %s", wsClassScript, codePath);
        } else if (OSinfo.isLinux()) {
            cmd = String.format("%s %s", wsClassScript, codePath);
        }

        log.info("开始执行代码编译脚本, 执行命令:{}", cmd);
        execCommand(cmd);
    }

    /**
     * 执行命令
     *
     * @param cmd
     */
    private void execCommand(String cmd) {
        log.info("执行命令开始:{}", cmd);
        try {
            Process process;
            if (OSinfo.isWindows()) {
                process = Runtime.getRuntime().exec(cmd);
            } else if (OSinfo.isLinux()) {
                process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd});
            } else {
                throw new MockException("不支持此操作系统");
            }
            process.waitFor();
            log.info("执行命令完成:{}", cmd);

            Thread.sleep(1000L);
        } catch (Exception e) {
            log.error("执行命令失败, cmd:{}", cmd, e);
        }
    }

    /**
     * 解析WebService客户端代码路径下文件，并生成接口实现类文件
     *
     * @param codeSrcPath 代码路径
     * @param codePackage 完整包名
     */
    private String analysisWsJavaFilePath(String codeSrcPath, String codePackage) {
        log.info("解析WebService生成代码路径下文件, 代码路径:{}, 文件包名:{}", codeSrcPath, codePackage);

        String fullCodePath = getCodeSrcFullPath(codePackage);
        log.info("WebService代码完整路径:{}", fullCodePath);
        File interFilePath = new File(fullCodePath);

        boolean isSuccess = false;
        try {
            int lastFileNum = 0;
            //最大检测次数
            int maxCheckTimes = 5;
            for (int i = 0; i < maxCheckTimes; i++) {
                File[] codeFiles = interFilePath.listFiles();
                if (codeFiles == null) {
                    Thread.sleep(1000);
                } else {
                    int fileNum = interFilePath.list().length;
                    if (lastFileNum == 0) {
                        lastFileNum = fileNum;
                        Thread.sleep(1000);
                    } else if (lastFileNum != fileNum) {
                        lastFileNum = interFilePath.list().length;
                        Thread.sleep(1000);
                    } else {
                        isSuccess = true;
                        break;
                    }
                }
                log.info("WebService客户端代码正在生成, 当前文件数量:{}", lastFileNum);
            }
        } catch (Exception e) {
            log.error("WebService检测代码生成是否完成失败", e);
        }

        if (!isSuccess) {
            log.error("WebService客户端代码生成失败");
            throw new MockException("WebService服务创建失败");
        }

        File[] codeFiles = interFilePath.listFiles();
        for (File file : codeFiles) {
            //检测文件是否是WebService接口
            boolean isInterface = isWebServiceInterface(file);
            if (!isInterface) {
                continue;
            }
            //WebService实现类代码生成
            return generateWsImplCode(file, codeSrcPath, codePackage);
        }
        return null;
    }

    /**
     * 解析WebService生成的文件，判断是否为客户端接口
     *
     * @param file
     */
    private boolean isWebServiceInterface(File file) {
        boolean isSuccess = false;
        boolean isWsInterface = false;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String lineString;
            while ((lineString = reader.readLine()) != null) {
                if (lineString.startsWith(Constants.JAVA_WS_ANNOTATION)) {
                    log.info("解析WebService客户端文件，该文件是WebService客户端接口文件, 文件名:{}", file.getName());
                    isWsInterface = true;
                    break;
                }
            }
            reader.close();
            isSuccess = true;
        } catch (IOException e) {
            log.error("解析WebService客户端文件异常, 文件名:{}", file.getName(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    log.error("解析WebService客户端文件，关闭文件流异常, 文件名:{}", file.getName(), e1);
                }
            }
        }

        if (!isSuccess) {
            throw new MockException("解析WebService客户端文件异常");
        }

        return isWsInterface;
    }

    /**
     * 重新加载class
     *
     * @param serviceNumber
     */
    private void reloadClass(int serviceNumber) {
        String codePackage = getCodePackage(serviceNumber);
        String classPath = getCodeClassFullPath(codePackage);
        //热加载class
        mockClassLoader.loadClassPath(classPath, codePackage);
    }

    /**
     * WebService实现类代码生成
     *
     * @param interfaceFile
     * @param codeSrcPath
     * @param codePackage
     * @return
     */
    private String generateWsImplCode(File interfaceFile, String codeSrcPath, String codePackage) {
        log.info("WebService实现类代码生成, 代码路径:{}, 代码包名：{}", codeSrcPath, codePackage);

        String interFileName = interfaceFile.getName();
        String interClassName = interFileName.split(Constants.STRING_SPLIT_POINT)[0];
        String implClassName = interClassName + Constants.JAVA_IMPL_NAME;
        String implFileName = implClassName + Constants.JAVA_EXT_NAME;

        //创建实现类所在文件夹
        File implPath = new File(codeSrcPath);
        if (!implPath.exists()) {
            implPath.mkdirs();
        }

        BufferedReader fileReader = null;
        FileWriter fileWriter = null;
        String implFullPath;
        try {
            //生成实现类代码
            implFullPath = getCodeSrcFullPath(codePackage) + File.separator + implFileName;
            log.info("WebService实现类代码生成开始, 实现类文件完整路径：{}", implFullPath);

            File implFile = new File(implFullPath);
            if (implFile.exists()) {
                implFile.delete();
            }
            implFile.createNewFile();

            fileReader = new BufferedReader(new FileReader(interfaceFile));
            fileWriter = new FileWriter(implFullPath, true);

            //写入包路径
            String lineString = Constants.JAVA_PACKAGE_BEGIN + codePackage + Constants.JAVA_END_STRING;
            fileWriter.write(lineString + Constants.JAVA_ENTER_STRING);
            fileWriter.write(Constants.JAVA_ENTER_STRING);
            //写入导入包
            fileWriter.write(Constants.JAVA_WEBSERVICE_PACKAGE + Constants.JAVA_ENTER_STRING);

            boolean isVoid = false;
            String curMethodReturnType = "";
            while ((lineString = fileReader.readLine()) != null) {
                if (lineString.startsWith(Constants.JAVA_PACKAGE_BEGIN)) {
                    continue;
                }
                if (lineString.startsWith(Constants.JAVA_IMPORT_BEGIN)) {
                    continue;
                }
                if (lineString.indexOf(Constants.JAVA_DOC_STAR) != -1) {
                    continue;
                }
                if (lineString.indexOf(Constants.JAVA_WEB_METHOD) != -1) {
                    isVoid = false;
                } else if (lineString.indexOf(Constants.JAVA_FUNCTION_VOID) != -1) {
                    isVoid = true;
                }
                if (lineString.indexOf(Constants.JAVA_METHOD_PUBLIC) != -1 && lineString.indexOf(Constants.JAVA_METHOD_BEGIN) != -1) {
                    String[] strs = lineString.split(" ");
                    int index = 0;
                    for (String str : strs) {
                        if (StringUtils.isEmpty(str)) {
                            continue;
                        }
                        if (index == 1) {
                            curMethodReturnType = str;
                            break;
                        }
                        index++;
                    }
                }
                List<String> lineList = getImplCodeLineList(codePackage, interClassName, implClassName, lineString, isVoid, curMethodReturnType);
                for (String lineStr : lineList) {
                    fileWriter.write(lineStr + Constants.JAVA_ENTER_STRING);
                }
            }
            log.info("WebService实现类代码生成完成, 实现类文件完整路径：{}", implFullPath);
        } catch (IOException e) {
            log.error("WebService实现类代码生成异常, 接口文件名:{}", interfaceFile.getName(), e);
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e1) {
                    log.error("WebService实现类代码生成，关闭文件流异常, 接口文件名:{}", interFileName, e1);
                }
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e1) {
                    log.error("WebService实现类代码生成，关闭文件流异常, 实现类文件名:{}", implFileName, e1);
                }
            }
        }
        return codePackage + Constants.STRING_POINT + implClassName;
    }

    /**
     * 获取实现类代码行内容
     *
     * @param codePackage
     * @param interClassName
     * @param implClassName
     * @param lineString
     * @param isVoid
     * @return
     */
    private List<String> getImplCodeLineList(String codePackage, String interClassName, String implClassName, String lineString, boolean isVoid, String returnType) {
        List<String> list = new ArrayList<>();
        if (lineString.startsWith(Constants.JAVA_WS_ANNOTATION)) {
            //写入注释
            list.add(Constants.JAVA_FILE_NOTE);
            //WebService实现类注解
            lineString = lineString.substring(0, lineString.length() - 1) + Constants.JAVA_WS_ENDPOINT + codePackage + Constants.STRING_POINT + interClassName + "\")";
            lineString = lineString.replaceAll(Constants.JAVA_WS_NAME, Constants.JAVA_WS_SERVICENAME);
            list.add(lineString);
            return list;
        }
        if (lineString.startsWith(Constants.JAVA_INTERFACE_BEGIN)) {
            //类名+实现的接口行
            lineString = Constants.JAVA_CLASS_BEGIN + implClassName + Constants.JAVA_IMPLEMENTS_NAME + interClassName + " " + Constants.JAVA_BODY_BEGIN;
            list.add(lineString);
            list.add("");
            //添加自定义方法
            list.add(Constants.JAVA_MOCK_RESULT_PROPERTY);
            list.add(Constants.JAVA_MOCK_RESULT_SET_METHOD);
            list.add(Constants.JAVA_MOCK_RESULT_GET_METHOD);
            return list;
        }
        if (lineString.indexOf(Constants.JAVA_WEB_METHOD) != -1) {
            //方法第一行增加@Override注解
            list.add(Constants.JAVA_OVERRIDE);
            return list;
        }
        if (lineString.indexOf(Constants.JAVA_ANNOTATION_BEGIN) != -1) {
            //跳过其他注解行
            return list;
        }
        if (lineString.indexOf(Constants.JAVA_FUNCTION_END) != -1) {
            //获取方法结束字符串
            lineString = getFunctionEndStr(lineString, isVoid, returnType);
            list.add(lineString);
            return list;
        }
        list.add(lineString);
        return list;
    }

    /**
     * 获取方法结束字符串
     * 重新加载服务的class
     *
     * @param lineString 代码行内容
     * @param isVoid     方法是否有返回值
     * @param returnType 方法返回类型
     * @return
     */
    private String getFunctionEndStr(String lineString, boolean isVoid, String returnType) {
        if (isVoid) {
            return lineString.replaceAll(Constants.JAVA_END_STRING, Constants.JAVA_RETURN_VOID);
        } else {
            return lineString.replaceAll(Constants.JAVA_END_STRING, String.format(Constants.JAVA_RETURN_OBJECT, returnType));
        }
    }

    /**
     * 设置Mock结果
     * 加载的是项目外部class文件，使用动态代理导致发布的WebService缺少注解无法调用，此处用调用自定义方法设置Mock结果
     *
     * @param obj
     * @param mockResult
     * @author jiayy
     */
    private void setMockResult(Object obj, Map<String, Object> mockResult) {
        try {
            if (mockResult == null) {
                return;
            }
            Method setResultMethod = obj.getClass().getMethod(Constants.METHOD_SET_MOCK_RESULT, Map.class);
            if (setResultMethod != null) {
                setResultMethod.invoke(obj, mockResult);
            }
        } catch (Throwable e) {
            log.error("设置WebService默认返回结果异常, className:{}", obj.getClass().getName(), e);
        }
    }
}
