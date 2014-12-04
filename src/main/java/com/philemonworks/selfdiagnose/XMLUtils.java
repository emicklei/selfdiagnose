/*
 Copyright 2006 Ernest Micklei @ PhilemonWorks.com

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 
 */
package com.philemonworks.selfdiagnose;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;

public class XMLUtils {

    public static String valueForXPath(String xpath, InputStream inputStream) throws XPathExpressionException {
        /*
         * Report.systemProperty("javax.xml.parsers.DocumentBuilderFactory");
         * Report.systemProperty("javax.xml.parsers.SAXParserFactory");
         * Report.systemProperty("javax.xml.transform.TransformerFactory");
         */
        XPathFactory factory = XPathFactory.newInstance();
        InputSource is = new InputSource(inputStream);
        XPath xp = factory.newXPath();
        return xp.evaluate(xpath, is);
    }

    /**
     * Performs entity encoding of characters from a String
     * http://stackoverflow.com/questions/439298/best-way-to-encode-text-data-for-xml-in-java
     * @param s : String
     * @return String
     */
    public static String encode(String originalUnprotectedString) {
        if (originalUnprotectedString == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < originalUnprotectedString.length(); i++) {
            char c = originalUnprotectedString.charAt(i);
            switch (c) {
            case '<':
                sb.append("&lt;");
                break;
            case '>':
                sb.append("&gt;");
                break;
            case '\"':
                sb.append("&quot;");
                break;
            case '&':
                sb.append("&amp;");
                break;
            case '\'':
                sb.append("&apos;");
                break;
            default:
                if (c > 0x7e) {
                    sb.append("&#" + ((int) c) + ";");
                } else
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String encode(Date someDate) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-d'T'HH:mm:ss.S");
        return df.format(someDate);
    }
}