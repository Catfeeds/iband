package com.manridy.iband.common;

import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DomXmlParse {
    public static class Image{
        public String id;
        public String least;
        public String file;
    }

    public static List<Image> parseXml(InputStream inStream) throws Exception{
        List<Image> imageList = new ArrayList<>();
        // 实例化一个文档构建器工厂
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // 通过文档构建器工厂获取一个文档构建器
        DocumentBuilder builder = factory.newDocumentBuilder();
        // 通过文档通过文档构建器构建一个文档实例
        Document document = builder.parse(inStream);
        //获取XML文件根节点
        Element root = document.getDocumentElement();
        //获得所有子节点
        NodeList childNodes = root.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++)
        {
            //遍历子节点
            Node childNode = (Node) childNodes.item(j);
            if (childNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Image image = new Image();
                Element childElement = (Element) childNode;
                image.id = childElement.getAttribute("id");
                image.least = childElement.getAttribute("least");
                image.file = childElement.getAttribute("file");
                imageList.add(image);
            }
        }
        return imageList;
    }
}