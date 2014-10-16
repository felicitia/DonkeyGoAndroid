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

import com.donkey.entity.XmlCommentItem;

public class XmlCommentItemHandler extends DefaultHandler {
	
	private List<XmlCommentItem> list = null;
	private XmlCommentItem commentItem = null;
	private String preTag;
	
	private StringBuffer memoryId;
	private StringBuffer memoryCommentId;
	private StringBuffer nickname;
	private StringBuffer mcContent;
	private StringBuffer mcPubdate;
	
	public List<XmlCommentItem> getCommentItems(InputStream xmlStream) throws Exception{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		XmlCommentItemHandler handler = new XmlCommentItemHandler();
		parser.parse(xmlStream, handler);
		return handler.getCommentItems();
	}
	
	public List<XmlCommentItem> getCommentItems(){
		return list;
	}
	
	@Override
	public void startDocument() throws SAXException{
		this.list = new ArrayList<XmlCommentItem>();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException{
		if("memorycomment".equals(qName)){
			commentItem = new XmlCommentItem();
			
			memoryId = new StringBuffer();
			memoryCommentId = new StringBuffer();
			nickname = new StringBuffer();
			mcContent = new StringBuffer();
			mcPubdate = new StringBuffer();
		}
		preTag = qName;
	}
	
	@Override
	public void characters(char[] ch,int start,int length) throws SAXException{
		if(preTag != null){
			String description = new String(ch,start,length);
			if("memoryid".equals(preTag)){
				memoryId.append(Html.fromHtml(description));
			}else if("memorycommentid".equals(preTag)){
				memoryCommentId.append(Html.fromHtml(description));
			}else if("nickname".equals(preTag)){
				nickname.append(Html.fromHtml(description));
			}else if("mccontent".equals(preTag)){
				mcContent.append(Html.fromHtml(description));
			}else if("mcpubdate".equals(preTag)){
				mcPubdate.append(Html.fromHtml(description));
			}
		}
	}
	
	@Override
	public void endElement(String uri,String localName,String qName)throws SAXException{
		if("memorycomment".equals(qName)){
			commentItem.setMcContent(mcContent.toString());
			commentItem.setMcPubdate(mcPubdate.toString());
			commentItem.setMemoryCommentId(memoryCommentId.toString());
			commentItem.setMemoryId(memoryId.toString());
			commentItem.setNickname(nickname.toString());
			
			list.add(commentItem);
			
			commentItem = null;
			
			mcContent = null;
			mcPubdate = null;
			memoryCommentId = null;
			memoryId = null;
			nickname = null;
		}
		preTag = null;
	}
}
