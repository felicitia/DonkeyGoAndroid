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

import com.donkey.entity.XmlGroupDetail;

public class XmlGroupDetailHandler extends DefaultHandler {
	private List<XmlGroupDetail> list = null;
	private XmlGroupDetail groupDetail = null;
	private String preTag = null;

	private StringBuffer memoryId;
	private StringBuffer title;
	private StringBuffer pubDate;
	private StringBuffer content;
	private StringBuffer location;
	private StringBuffer nickname;
	private StringBuffer avatar;
	private StringBuffer state;
	private StringBuffer friendId;

	public List<XmlGroupDetail> getGroupDetails(InputStream xmlStream)
			throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		XmlGroupDetailHandler handler = new XmlGroupDetailHandler();
		parser.parse(xmlStream, handler);
		return handler.getGroupDetails();
	}

	public List<XmlGroupDetail> getGroupDetails() {
		return list;
	}

	@Override
	public void startDocument() throws SAXException {
		this.list = new ArrayList<XmlGroupDetail>();
		Log.i("detail", "start document");
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		Log.i("detail", "start element");
		if ("memory".equals(qName)) {
			groupDetail = new XmlGroupDetail();

			memoryId = new StringBuffer();
			avatar = new StringBuffer();
			pubDate = new StringBuffer();
			title  = new StringBuffer();
			content  = new StringBuffer();
			location  = new StringBuffer();
			nickname  = new StringBuffer();
			state  = new StringBuffer();
			friendId  = new StringBuffer();
		}
		preTag = qName;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		Log.i("detail" , "characters");
		if (preTag != null) {
			Log.i("pre tag", "not null");
			String description = new String(ch, start, length);
			if ("mid".equals(preTag)) {
				memoryId.append(Html.fromHtml(description));
			} else if ("title".equals(preTag)) {
				title.append(Html.fromHtml(description));
			} else if ("location".equals(preTag)) {
				location.append(Html.fromHtml(description));
			}  else if ("content".equals(preTag)) {
			content.append(Html.fromHtml(description));
			} else if ("pubdate".equals(preTag)) {
			pubDate.append(Html.fromHtml(description));
			} else if ("nickname".equals(preTag)) {
			nickname.append(Html.fromHtml(description));
			} else if ("avatar".equals(preTag)) {
			avatar.append(Html.fromHtml(description));
			} else if ("state".equals(preTag)) {
			state.append(Html.fromHtml(description));
			} else if ("fid".equals(preTag)) {
			friendId.append(Html.fromHtml(description));
			} 
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		Log.i("detail" ,"end element");
		if ("memory".equals(qName)) {
			Log.i("detail", "memory");
			groupDetail.setAvatar(avatar.toString());
			groupDetail.setContent(content.toString());
			groupDetail.setFriendId(friendId.toString());
			groupDetail.setLocation(location.toString());
			groupDetail.setMemoryId(memoryId.toString());
			groupDetail.setNickname(nickname.toString());
			groupDetail.setPubDate(pubDate.toString());
			groupDetail.setState(state.toString());
			groupDetail.setTitle(title.toString());

			list.add(groupDetail);

			groupDetail = null;

			memoryId = null;
			avatar = null;
			pubDate = null;
			title  = null;
			content  = null;
			location  = null;
			nickname  = null;
			state  = null;
			friendId  = null;
		}
		preTag = null;
	}
}
