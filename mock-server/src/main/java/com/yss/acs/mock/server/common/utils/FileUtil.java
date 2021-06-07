package com.yss.acs.mock.server.common.utils;

import lombok.extern.slf4j.Slf4j;
import com.yss.acs.mock.server.common.exception.MockException;

import java.io.*;

/**
 * 文件处理工具类
 *
 * @author jiayy
 * @date 2020/7/8
 */
@Slf4j
public class FileUtil {

    /**
     * 迭代删除文件夹
     *
     * @param dirPath 文件夹路径
     */
    public static void deleteDir(String dirPath) {
        File file = new File(dirPath);
        if (file.isFile()) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            if (files == null) {
                file.delete();
            } else {
                for (int i = 0; i < files.length; i++) {
                    deleteDir(files[i].getAbsolutePath());
                }
                file.delete();
            }
        }
    }

    /**
     * 读取文件内容
     *
     * @param filePath
     * @return
     */
    public static String readFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(filePath)));
            String lineString;
            while ((lineString = reader.readLine()) != null) {
                sb.append(lineString);
            }
        } catch (FileNotFoundException e) {
            log.warn("读取文件不存在, filePath:{}", e);
            throw new MockException("读取文件不存在");
        } catch (Exception e) {
            log.warn("读取文件异常, filePath:{}", e);
            throw new MockException("读取文件异常");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * 写文件
     *
     * @param filePath
     * @param filePath
     * @param content
     */
    public static void writeFile(String filePath, String fileName, String content) {
        PrintWriter out = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            out = new PrintWriter(new BufferedWriter(new
                    OutputStreamWriter(new FileOutputStream(file), "utf-8")));

            out.write(content);
            out.flush();
        } catch (Exception e) {
            log.error("文件写入失败, filePath:{}", filePath, e);
            throw new MockException("文件写入失败");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
