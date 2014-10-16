package com.donkey.httpclient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.text.Html;
import android.util.Log;

import com.donkey.entity.XmlGroupItem;

public class XmlGroupItemHandler extends DefaultHandler {
	private List<XmlGroupItem> list = null;
	private XmlGroupItem groupItem = null;
	private String preTag = null;

	private StringBuffer gid;
	private StringBuffer gname;
	private StringBuffer gnum;
	private StringBuffer gonline;

	public List<XmlGroupItem> getGroupItems(InputStream xmlStream)
			throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		XmlGroupItemHandler handler = new XmlGroupItemHandler();
		parser.parse(xmlStream, handler);
		return handler.getGroupItems();
	}

	public List<XmlGroupItem> getGroupItems() {
		return list;
	}

	@Override
	public void startDocument() throws SAXException {
		this.list = new ArrayList<XmlGroupItem>();
		Log.i("xml", "start document");
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		Log.i("xml", "start element");
		if ("group".equals(qName)) {
			groupItem = new XmlGroupItem();

			gid = new StringBuffer();
			gname = new StringBuffer();
			gnum = new StringBuffer();
			gonline = new StringBuffer();
		}
		preTag = qName;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		Log.i("xml" , "characters");
		if (preTag != null) {
			Log.i("pre tag", "not null");
			String description = new String(ch, start, length);
			if ("gid".equals(preTag)) {
				gid.append(Html.fromHtml(description));
			} else if ("gname".equals(preTag)) {
				gname.append(Html.fromHtml(description));
			} else if ("gnum".equals(preTag)) {
				gnum.append(Html.fromHtml(description));
			}  else if ("gonline".equals(preTag)) {
			gonline.append(Html.fromHtml(description));
		} 
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		Log.i("xml" ,"end element");
		if ("group".equals(qName)) {
			Log.i("xml", "group");
			groupItem.setGid(gid.toString());
			groupItem.setGname(gname.toString());
			groupItem.setGnum(gnum.toString());
			groupItem.setGonline(gonline.toString());

			list.add(groupItem);

			groupItem = null;

			gid = null;
			gname = null;
			gnum = null;
			gonline = null;
		}
		preTag = null;
	}
}
