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

import com.donkey.entity.XmlMemoryItem;

public class XmlMemoryItemHandler extends DefaultHandler {
	private List<XmlMemoryItem> list = null;
	private XmlMemoryItem memoryItem = null;
	private String preTag = null;

	private StringBuffer memoryId;
	private StringBuffer title;
	private StringBuffer pubDate;
	private StringBuffer content;
	private StringBuffer location;
	private StringBuffer nickname;
	private StringBuffer avatar;
	private StringBuffer avgScore;

	public List<XmlMemoryItem> getMemoryItems(InputStream xmlStream)
			throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		XmlMemoryItemHandler handler = new XmlMemoryItemHandler();
		parser.parse(xmlStream, handler);
		return handler.getMemoryItems();
	}

	public List<XmlMemoryItem> getMemoryItems() {
		return list;
	}

	@Override
	public void startDocument() throws SAXException {
		this.list = new ArrayList<XmlMemoryItem>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if ("item".equals(qName)) {
			memoryItem = new XmlMemoryItem();

			memoryId = new StringBuffer();
			title = new StringBuffer();
			pubDate = new StringBuffer();
			content = new StringBuffer();
			location = new StringBuffer();
			nickname = new StringBuffer();
			avatar = new StringBuffer();
			avgScore = new StringBuffer();
		}
		preTag = qName;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (preTag != null) {
			String description = new String(ch, start, length);
			if ("memoryid".equals(preTag)) {
				memoryId.append(Html.fromHtml(description));
			} else if ("title".equals(preTag)) {
				title.append(Html.fromHtml(description));
			} else if ("pubdate".equals(preTag)) {
				pubDate.append(Html.fromHtml(description));
			} else if ("content".equals(preTag)) {
				content.append(Html.fromHtml(description));
			} else if ("location".equals(preTag)) {
				location.append(Html.fromHtml(description));
			} else if ("nickname".equals(preTag)) {
				nickname.append(Html.fromHtml(description));
			} else if ("avatar".equals(preTag)) {
				avatar.append(Html.fromHtml(description));
			} else if ("avgscore".equals(preTag)) {
				avgScore.append(Html.fromHtml(description));
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if ("item".equals(qName)) {
			memoryItem.setAvatar(avatar.toString());
			memoryItem.setAvgScore(avgScore.toString());
			memoryItem.setContent(content.toString());
			memoryItem.setLocation(location.toString());
			memoryItem.setMemoryId(memoryId.toString());
			memoryItem.setNickname(nickname.toString());
			memoryItem.setPubDate(pubDate.toString());
			memoryItem.setTitle(title.toString());

			list.add(memoryItem);

			memoryItem = null;

			memoryId = null;
			title = null;
			pubDate = null;
			content = null;
			location = null;
			nickname = null;
			avatar = null;
			avgScore = null;
		}
		preTag = null;
	}
}
