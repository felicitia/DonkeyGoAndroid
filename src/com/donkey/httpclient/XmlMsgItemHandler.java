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

import com.donkey.entity.XmlMsgItem;

public class XmlMsgItemHandler extends DefaultHandler {

	private List<XmlMsgItem> list = null;
	private XmlMsgItem msgItem = null;
	private String preTag;

	private StringBuffer id;
	private StringBuffer friendId;
	private StringBuffer time;
	private StringBuffer content;
	private StringBuffer avatar;
	private StringBuffer nickname;

	public List<XmlMsgItem> getMsgItems(InputStream xmlStream) throws Exception{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		XmlMsgItemHandler handler = new XmlMsgItemHandler();
		parser.parse(xmlStream, handler);
		return handler.getMsgItems();
	}
	
	public List<XmlMsgItem> getMsgItems(){
		return list;
	}
	
	@Override
	public void startDocument() throws SAXException {
		this.list = new ArrayList<XmlMsgItem>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if ("item".equals(qName)) {
			msgItem = new XmlMsgItem();

			id = new StringBuffer();
			friendId = new StringBuffer();
			time = new StringBuffer();
			content = new StringBuffer();
			avatar = new StringBuffer();
			nickname = new StringBuffer();
		}
		preTag = qName;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (preTag != null) {
			String description = new String(ch, start, length);
			if ("id".equals(preTag)) {
				id.append(Html.fromHtml(description));
			} else if ("friendid".equals(preTag)) {
				friendId.append(Html.fromHtml(description));
			} else if ("avatar".equals(preTag)) {
				avatar.append(Html.fromHtml(description));
			} else if ("time".equals(preTag)) {
				time.append(Html.fromHtml(description));
			} else if ("content".equals(preTag)) {
				content.append(Html.fromHtml(description));
			}else if("nickname".equals(preTag)){
				nickname.append(Html.fromHtml(description));
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if ("item".equals(qName)) {
			msgItem.setAvatar(avatar.toString());
			msgItem.setContent(content.toString());
			msgItem.setFriendId(friendId.toString());
			msgItem.setId(id.toString());
			msgItem.setTime(time.toString());
			msgItem.setNickname(nickname.toString());
			
			list.add(msgItem);
			
			msgItem = null;
			
			id = null;
			avatar = null;
			content = null;
			friendId = null;
			time = null;
		}
		preTag = null;
	}
}
