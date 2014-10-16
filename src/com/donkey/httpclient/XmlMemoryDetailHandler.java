package com.donkey.httpclient;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.os.Bundle;
import android.text.Html;

public class XmlMemoryDetailHandler extends DefaultHandler {
	
	private Bundle bundle;
	private String preTag;
	
	private StringBuffer memoryId;
	private StringBuffer title;
	private StringBuffer pubDate;
	private StringBuffer content;
	private StringBuffer location;
	private StringBuffer landmarkCount;
	private StringBuffer commentCount;
	private StringBuffer authorId; 
	private StringBuffer avgScore;
	
	public Bundle getMemoryDetails(InputStream xmlStream) throws Exception{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		XmlMemoryDetailHandler handler = new XmlMemoryDetailHandler();
		parser.parse(xmlStream, handler);
		return handler.getMemoryDetails();
	}
	
	public Bundle getMemoryDetails(){
		return bundle;
	}
	
	@Override
	public void startDocument() throws SAXException {
		this.bundle = new Bundle();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if("item".equals(qName)){
			memoryId = new StringBuffer();
			title = new StringBuffer();
			pubDate = new StringBuffer();
			content = new StringBuffer();
			location = new StringBuffer();
			landmarkCount = new StringBuffer();
			commentCount = new StringBuffer();
			authorId = new StringBuffer();
			avgScore = new StringBuffer();
		}
		preTag = qName;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String description = new String(ch,start,length);
		if("memoryid".equals(preTag)){
			memoryId.append(Html.fromHtml(description));
		}else if("title".equals(preTag)){
			title.append(Html.fromHtml(description));
		}else if("pubdate".equals(preTag)){
			pubDate.append(Html.fromHtml(description));
		}else if("content".equals(preTag)){
			content.append(Html.fromHtml(description));
		}else if("location".equals(preTag)){
			location.append(Html.fromHtml(description));
		}else if("landmarkcount".equals(preTag)){
			landmarkCount.append(Html.fromHtml(description));
		}else if("commentcount".equals(preTag)){
			commentCount.append(Html.fromHtml(description));
		}else if("authorid".equals(preTag)){
			authorId.append(Html.fromHtml(description));
		}else if("avgscore".equals(preTag)){
			avgScore.append(Html.fromHtml(description));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if("item".equals(qName)){
			this.bundle.putString("authorId", authorId.toString());
			this.bundle.putString("commentCount", commentCount.toString());
			this.bundle.putString("content", content.toString());
			this.bundle.putString("landmarkCount", landmarkCount.toString());
			this.bundle.putString("location", location.toString());
			this.bundle.putString("memoryId", memoryId.toString());
			this.bundle.putString("pubDate", pubDate.toString());
			this.bundle.putString("title", title.toString());
			this.bundle.putString("avgScore", avgScore.toString());
			
			authorId = null;
			commentCount = null;
			content = null;
			landmarkCount = null;
			location = null;
			memoryId = null;
			pubDate = null;
			title = null;
		}
		preTag = null;
	}
}
