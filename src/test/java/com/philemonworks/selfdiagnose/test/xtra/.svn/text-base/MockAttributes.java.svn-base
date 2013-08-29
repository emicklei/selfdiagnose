package com.philemonworks.selfdiagnose.test.xtra;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

public class MockAttributes extends Object implements Attributes {
	private Map map = new HashMap();
	public void put(String key, String value) {
		map.put(key, value);
	}
	public int getIndex(String qName) {
		return 0;
	}
	public int getIndex(String uri, String localName) {
		return 0;
	}
	public int getLength() {
		return map.size();
	}
	public String getLocalName(int index) {
		return null;
	}
	public String getQName(int index) {
		return null;
	}
	public String getType(int index) {
		return null;
	}
	public String getType(String qName) {
		return null;
	}
	public String getType(String uri, String localName) {
		return null;
	}
	public String getURI(int index) {
		return null;
	}
	public String getValue(int index) {
		return null;
	}
	public String getValue(String qName) {
		return (String) map.get(qName);
	}
	public String getValue(String uri, String localName) {
		return null;
	}
}
