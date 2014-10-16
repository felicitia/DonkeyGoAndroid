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

import com.donkey.entity.XmlTravelItem;

public class XmlTravelItemHandler extends DefaultHandler {

	private List<XmlTravelItem> list = null;
	private XmlTravelItem travelItem = null;
	private String preTag;

	private StringBuffer uid;
	private StringBuffer travelId;
	private StringBuffer travelTitle;
	private StringBuffer travelPubdate;
	private StringBuffer startTime;
	private StringBuffer lasting;
	private StringBuffer travelContent;
	private StringBuffer travelLocation;
	private StringBuffer username;
	private StringBuffer avatar;

	public List<XmlTravelItem> getTravelItems(InputStream xmlStream) throws Exception{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		XmlTravelItemHandler handler = new XmlTravelItemHandler();
		parser.parse(xmlStream, handler);
		return handler.getTravelItems();
	}
	
	public List<XmlTravelItem> getTravelItems(){
		return list;
	}
	
	@Override
	public void startDocument() throws SAXException {
		this.list = new ArrayList<XmlTravelItem>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if ("item".equals(qName)) {
			travelItem = new XmlTravelItem();

			uid = new StringBuffer();
			travelContent = new StringBuffer();
			travelId = new StringBuffer();
			travelLocation = new StringBuffer();
			travelPubdate = new StringBuffer();
			travelTitle = new StringBuffer();
			startTime = new StringBuffer();
			lasting = new StringBuffer();
			username = new StringBuffer();
			avatar = new StringBuffer();
		}
		preTag = qName;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (preTag != null) {
			String description = new String(ch, start, length);
			if ("travelid".equals(preTag)) {
				travelId.append(Html.fromHtml(description));
			} else if ("uid".equals(preTag)) {
				uid.append(Html.fromHtml(description));
			} else if ("traveltitle".equals(preTag)) {
				travelTitle.append(Html.fromHtml(description));
			} else if ("starttime".equals(preTag)) {
				startTime.append(Html.fromHtml(description));
			} else if ("lasting".equals(preTag)) {
				lasting.append(Html.fromHtml(description));
			} else if ("travelpubdate".equals(preTag)) {
				travelPubdate.append(Html.fromHtml(description));
			} else if ("travelcontent".equals(preTag)) {
				travelContent.append(Html.fromHtml(description));
			} else if ("travellocation".equals(preTag)) {
				travelLocation.append(Html.fromHtml(description));
			}else if("username".equals(preTag)){
				username.append(Html.fromHtml(description));
			}else if("avatar".equals(preTag)){
				avatar.append(Html.fromHtml(description));
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if ("item".equals(qName)) {
			travelItem.setLasting(lasting.toString());
			travelItem.setStartTime(startTime.toString());
			travelItem.setTravelContent(travelContent.toString());
			travelItem.setTravelId(travelId.toString());
			travelItem.setTravelLocation(travelLocation.toString());
			travelItem.setTravelPubdate(travelPubdate.toString());
			travelItem.setTravelTitle(travelTitle.toString());
			travelItem.setUid(uid.toString());
			travelItem.setUsername(username.toString());
			travelItem.setAvatar(avatar.toString());

			list.add(travelItem);

			travelItem = null;
			travelContent = null;
			travelId = null;
			travelLocation = null;
			travelPubdate = null;
			travelTitle = null;
			startTime = null;
			lasting = null;
			uid = null;
			username = null;
			avatar = null;
		}
		preTag = null;
	}
}
