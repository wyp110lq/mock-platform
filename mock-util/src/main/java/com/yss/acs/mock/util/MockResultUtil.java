package com.yss.acs.mock.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Mock结果处理工具类
 *
 * @author jiayy
 * @date 2020/9/8
 */
public class MockResultUtil {

    private static final String INTEGERS = "123456789";

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";

    /**
     * 默认长度
     */
    private static final int DEFAULT_LENGTH = 15;

    /**
     * 最大长度
     */
    private static final int MAX_LENGTH = 100;

    /**
     * 格式：年－月－日 小时：分钟：秒
     */
    private static final String FORMAT_ONE = "yyyy-MM-dd HH:mm:ss";

    /**
     * 格式：年－月－日
     */
    private static final String LONG_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 正整数正则表达式
     */
    private static final Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");

    public static void main(String[] args) {
        System.out.println(isInteger("aa"));
    }

    /**
     * 获取特殊字符返回结果
     *
     * @param resResult
     * @return
     */
    public static String getMockResult(String resResult) {
        if (isEmpty(resResult)) {
            return "";
        }
        //随机整数
        if (resResult.contains(MockKeyword.RANDOM_INT.getCode())) {
            resResult = transformKeyword(resResult, MockKeyword.RANDOM_INT);
        }
        //随机字符串
        if (resResult.contains(MockKeyword.RANDOM_STRING.getCode())) {
            resResult = transformKeyword(resResult, MockKeyword.RANDOM_STRING);
        }
        //随机布尔值
        if (resResult.contains(MockKeyword.RANDOM_BOOLEAN.getCode())) {
            resResult = resResult.replaceAll(MockKeyword.RANDOM_BOOLEAN.getCode(), String.valueOf(Integer.parseInt(getRandomIntStr(1)) % 2 == 0));
        }
        //当前时间
        if (resResult.contains(MockKeyword.DATETIME.getCode())) {
            resResult = resResult.replaceAll(MockKeyword.DATETIME.getCode(), getCurrDate(FORMAT_ONE));
        }
        //当前日期
        if (resResult.contains(MockKeyword.DATE.getCode())) {
            resResult = resResult.replaceAll(MockKeyword.DATE.getCode(), getCurrDate(LONG_DATE_FORMAT));
        }
        return resResult;
    }

    /**
     * 获取Mock结果Object
     *
     * @param resResult
     * @return
     */
    public static Object getMockResultObject(Object resResult) {
        if (resResult == null) {
            return null;
        }
        if (resResult instanceof String) {
            return getMockResult((String) resResult);
        }
        if (resResult instanceof Map) {
            return resResult;
        }
        if (resResult instanceof Collection) {
            return resResult;
        }

        if (resResult instanceof Object) {
            Object mockObj;
            try {
                mockObj = resResult.getClass().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return resResult;
            }
            Field[] fields = mockObj.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                try {
                    Object fieldValue = field.get(resResult);
                    if (fieldValue instanceof String) {
                        String mockResult = getMockResult((String) fieldValue);
                        field.set(mockObj, mockResult);
                    } else {
                        field.set(mockObj, fieldValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return mockObj;
        }
        return resResult;
    }

    /**
     * 转换关键字
     *
     * @param resResult
     * @param keyword
     * @return
     */
    private static String transformKeyword(String resResult, MockKeyword keyword) {
        String keyCode = keyword.getCode();
        if (resResult.indexOf(keyCode) == -1) {
            return resResult;
        }
        int length = 0;
        String beginIndexStr = keyCode + "(";
        int beginIndex = resResult.indexOf(beginIndexStr);
        if (beginIndex != -1) {
            String tempString = resResult.substring(beginIndex + beginIndexStr.length());
            int endIndex = tempString.indexOf(")");
            if (endIndex != -1) {
                String lengthStr = tempString.substring(0, endIndex);
                if (isEmpty(lengthStr)) {
                    length = DEFAULT_LENGTH;
                    keyCode += "()";
                } else if (isInteger(lengthStr)) {
                    length = Integer.parseInt(lengthStr);
                    keyCode += "(" + lengthStr + ")";
                }
            }
        }
        if (length <= 0) {
            length = DEFAULT_LENGTH;
        }
        if (length > MAX_LENGTH) {
            length = MAX_LENGTH;
        }
        if (MockKeyword.RANDOM_INT.equals(keyword)) {
            resResult = resResult.replace(keyCode, getRandomIntStr(length));
        } else if (MockKeyword.RANDOM_STRING.equals(keyword)) {
            resResult = resResult.replace(keyCode, getRandomStr(length));
        } else {
            return resResult;
        }
        return transformKeyword(resResult, keyword);
    }

    /**
     * 获取指定位数随机字符串
     *
     * @param len
     * @return
     */
    private static String getRandomStr(int len) {
        Random random1 = new Random();
        //指定字符串长度，拼接字符并toString
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            //获取指定长度的字符串中任意一个字符的索引值
            int number = random1.nextInt(CHARACTERS.length());
            //根据索引值获取对应的字符
            char charAt = CHARACTERS.charAt(number);
            sb.append(charAt);
        }
        return sb.toString();
    }

    /**
     * 获取指定位数随机数
     *
     * @param len
     * @return
     */
    private static String getRandomIntStr(int len) {
        Random random1 = new Random();
        //指定字符串长度，拼接字符并toString
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            //获取指定长度的字符串中任意一个字符的索引值
            int number = random1.nextInt(INTEGERS.length());
            //根据索引值获取对应的字符
            char charAt = INTEGERS.charAt(number);
            sb.append(charAt);
        }
        return sb.toString();
    }

    private static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    /**
     * 是否是整数
     *
     * @param str
     * @return
     */
    private static boolean isInteger(String str) {
        return pattern.matcher(str).matches();
    }

    /**
     * 获取当前时间的日期格式
     *
     * @param format
     * @return
     */
    private static String getCurrDate(String format) {
        return dateToString(new Date(), format);
    }

    /**
     * 日期转字符串
     *
     * @param date
     * @return
     */
    private static String dateToString(Date date, String format) {
        String result = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            result = dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
