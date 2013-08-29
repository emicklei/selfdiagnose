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

	public static String valueForXPath(String xpath, InputStream inputStream)
			throws XPathExpressionException {
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
	 * @param s : String
	 * @return String
	 */
	public static String encode(String s) {
		StringBuffer result = null;
		for (int i = 0, max = s.length(), delta = 0; i < max; i++) {
			char c = s.charAt(i);
			String replacement = null;
			if (c == '&') {
				replacement = "&amp;";
			} else if (c == '<') {
				replacement = "&lt;";
			} else if (c == '\r') {
				replacement = "&#13;";
			} else if (c == '>') {
				replacement = "&gt;";
			} else if (c == '"') {
				replacement = "&quot;";
			} else if (c == '\'') {
				replacement = "&apos;";
			}
			if (replacement != null) {
				if (result == null) {
					result = new StringBuffer(s);
				}
				result.replace(i + delta, i + delta + 1, replacement);
				delta += (replacement.length() - 1);
			}
		}
		if (result == null) {
			return s;
		}
		return result.toString();
	}
	
	public static String encode(Date someDate) {
	    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-d'T'HH:mm:ss.S");
	    return df.format(someDate);
	}
}