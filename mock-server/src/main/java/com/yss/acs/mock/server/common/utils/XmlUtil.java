package com.yss.acs.mock.server.common.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;

/**
 * Xml工具类
 *
 * @author jiayy
 * @date 2020/6/30
 */
public class XmlUtil {

    /**
     * 加载xml
     *
     * @param xml
     * @return
     * @throws DocumentException
     */
    public static Document loadXml(String xml) throws DocumentException {

        SAXReader reader = new SAXReader();
        return reader.read(new ByteArrayInputStream(xml.getBytes()));
    }
}
