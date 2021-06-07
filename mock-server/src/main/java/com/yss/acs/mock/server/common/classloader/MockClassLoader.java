package com.yss.acs.mock.server.common.classloader;

import com.yss.acs.mock.server.common.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Class动态加载类
 *
 * @author jiayy
 * @date 2020/7/4
 */
@Component
@Slf4j
public class MockClassLoader extends ClassLoader {

    /**
     * 资源缓存
     */
    public static Map<String, Object> resources = new ConcurrentHashMap<>();

    /**
     * class缓存
     */
    private static Map<String, Class> cacheClassMap = new ConcurrentHashMap<>();

    public Class getClass(String className) {
        return cacheClassMap.get(className);
    }

    /**
     * 热加载路径下所有class
     *
     * @param classpath
     */
    public void loadClassPath(String classpath, String packageName) {
        File filePath = new File(classpath);
        if (!filePath.exists()) {
            return;
        }
        //如果存在package-info.class文件，优先加载
        for (File file : filePath.listFiles()) {
            if (!file.getName().endsWith(".class")) {
                continue;
            }
            if (file.getName().equals(Constants.FILE_PACKAGE_INFO)) {
                String className = packageName + Constants.STRING_POINT + file.getName().split(Constants.STRING_SPLIT_POINT)[0];
                if (cacheClassMap.containsKey(className)) {
                    continue;
                }
                loadClass(file, className);
                //定义包信息
                super.definePackage(packageName, null, null, null, null, null, null, null);
            }
        }
        //加载其他class
        for (File file : filePath.listFiles()) {
            if (!file.getName().endsWith(".class")) {
                continue;
            }
            if (file.getName().equals(Constants.FILE_PACKAGE_INFO)) {
                continue;
            }
            String className = packageName + Constants.STRING_POINT + file.getName().split(Constants.STRING_SPLIT_POINT)[0];
            if (cacheClassMap.containsKey(className)) {
                continue;
            }
            loadClass(file, className);
        }
    }

    /**
     * @param classFile class文件
     * @return
     * @throws Exception
     */
    public Class<?> loadClass(File classFile, String className) {
        byte[] bytes = new byte[(int) classFile.length()];
        FileInputStream fis = null;
        Class<?> clazz = null;
        try {
            fis = new FileInputStream(classFile);
            int j = 0;
            while (true) {
                int i = fis.read(bytes);
                if (i == -1) {
                    break;
                }
                j += i;
            }
            clazz = super.defineClass(className, bytes, 0, j);
            //加入缓存
            cacheClassMap.put(clazz.getName(), clazz);
        } catch (FileNotFoundException e) {
            log.error("加载Class异常", e);
        } catch (IOException e) {
            log.error("加载Class异常", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error("加载Class关闭流异常", e);
                }
            }
        }
        return clazz;
    }

    /**
     * 加载jar包
     *
     * @param jarFile
     * @throws Exception
     */
    public void loadJar(File jarFile) throws Exception {
        ArrayList classNames = new ArrayList();
        ArrayList classBuffers = new ArrayList();
        // 存储依赖类
        HashMap depends = new HashMap(10);
        JarInputStream jar = new JarInputStream(new FileInputStream(jarFile));
        // 依次获得对应JAR文件中封装的各个被压缩文件的JarEntry
        JarEntry entry;
        while ((entry = jar.getNextJarEntry()) != null) {
            // 当找到的entry为class时
            if (entry.getName().toLowerCase().endsWith(".class")) {
                // 将类路径转变为类全称
                String name = entry.getName().substring(0,
                        entry.getName().length() - ".class".length()).replace(
                        '/', '.');
                // 加载该类
                byte[] data = getResourceData(jar);
                // 缓存类名及数据
                classNames.add(name);
                classBuffers.add(data);

            } else {
                // 非class结尾但开头字符为'/'时
                if (entry.getName().charAt(0) == '/') {
                    resources.put(entry.getName(), getResourceData(jar));
                    // 否则追加'/'后缓存
                } else {
                    resources.put("/" + entry.getName(), getResourceData(jar));
                }
            }
        }
        //当获得的classNames名不为空时
        while (classNames.size() > 0) {
            //获得类路径全长
            int n = classNames.size();
            for (int i = classNames.size() - 1; i >= 0; i--) {
                try {
                    //查询指定类
                    super.defineClass((String) classNames.get(i),
                            (byte[]) classBuffers.get(i), 0,
                            ((byte[]) classBuffers.get(i)).length);
                    //获得类名
                    String pkName = (String) classNames.get(i);
                    if (pkName.lastIndexOf('.') >= 0) {
                        pkName = pkName
                                .substring(0, pkName.lastIndexOf('.'));
                        if (super.getPackage(pkName) == null) {
                            super.definePackage(pkName, null, null, null,
                                    null, null, null, null);
                        }
                    }
                    //查询后删除缓冲
                    classNames.remove(i);
                    classBuffers.remove(i);
                } catch (NoClassDefFoundError e) {
                    depends.put(classNames.get(i), e.getMessage()
                            .replaceAll("/", "."));
                } catch (UnsupportedClassVersionError e) {
                    //jre版本错误提示
                    throw new UnsupportedClassVersionError(classNames.get(i)
                            + ", " + System.getProperty("java.vm.name") + " "

                            + System.getProperty("java.vm.version") + ")");
                }
            }
            if (n == classNames.size()) {
                for (int i = 0; i < classNames.size(); i++) {
                    log.error("NoClassDefFoundError:{}", classNames.get(i));
                    String className = (String) classNames.get(i);
                    while (depends.containsKey(className)) {
                        className = (String) depends.get(className);
                    }
                }
                break;
            }
        }
    }

    /**
     * 获得指定路径文件的byte[]形式
     *
     * @param name
     * @return
     */
    final static public byte[] getDataSource(String name) {
        FileInputStream fileInput;
        try {
            fileInput = new FileInputStream(new File(name));
        } catch (FileNotFoundException e) {
            fileInput = null;
        }
        BufferedInputStream bufferedInput = new BufferedInputStream(fileInput);
        return getDataSource(bufferedInput);
    }

    /**
     * 获得指定InputStream的byte[]形式
     *
     * @param is
     * @return
     */
    final static public byte[] getDataSource(InputStream is) {
        if (is == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] arrayByte = null;
        try {
            byte[] bytes = new byte[8192];
            bytes = new byte[is.available()];
            int read;
            while ((read = is.read(bytes)) >= 0) {
                byteArrayOutputStream.write(bytes, 0, read);
            }
            arrayByte = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
                if (is != null) {
                    is.close();
                }

            } catch (IOException e) {
            }
        }
        return arrayByte;
    }

    /**
     * 获得指定JarInputStream的byte[]形式
     *
     * @param jar
     * @return
     * @throws IOException
     */
    final private byte[] getResourceData(JarInputStream jar)
            throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int size;
        while (jar.available() > 0) {
            size = jar.read(buffer);
            if (size > 0) {
                data.write(buffer, 0, size);
            }
        }
        return data.toByteArray();
    }

    /**
     * 重载的getResource,检查是否重复包含
     */
    @Override
    public URL getResource(String name) {
        if (resources.containsKey("/" + name)) {
            try {
                return new URL("file:///" + name);
            } catch (MalformedURLException e) {
                log.error("执行getResource方法异常", e);
            }
        }
        return super.getResource(name);
    }

    /**
     * 执行指定class类
     *
     * @param clz
     * @param methodName
     * @param args
     */
    public static void callVoidMethod(Class clz, String methodName,
                                      String[] args) {
        Class[] arg = new Class[1];
        arg[0] = args.getClass();
        try {
            Method method = clz.getMethod(methodName, arg);
            Object[] inArg = new Object[1];
            inArg[0] = args;
            method.invoke(clz, inArg);
        } catch (Exception e) {
            log.error("执行指定class类方法异常", e);
        }
    }

    /**
     * 重载的getResourceAsStream,检查是否重复包含
     *
     * @param name
     * @return
     */
    @Override
    public InputStream getResourceAsStream(String name) {
        if (name.charAt(0) == '/') {
            name = name.substring(1);
        }
        if (resources.containsKey("/" + name)) {
            return new ByteArrayInputStream((byte[]) resources.get("/" + name));
        }
        return super.getResourceAsStream(name);
    }
}
