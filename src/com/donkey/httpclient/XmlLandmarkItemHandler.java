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

import com.donkey.entity.LandMarkItem;

public class XmlLandmarkItemHandler extends DefaultHandler {
	private List<LandMarkItem> list;
	private LandMarkItem lmItem;
	private String preTag;

	private StringBuffer lmId;
	private StringBuffer memId;
	private StringBuffer lmName;
	private StringBuffer lmType;
	private StringBuffer mediaType;
	private StringBuffer mediaPath;
	private StringBuffer longtitude;
	private StringBuffer latitude;

	public List<LandMarkItem> getLandMarkItems(InputStream xmlStream)
			throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		XmlLandmarkItemHandler handler = new XmlLandmarkItemHandler();
		parser.parse(xmlStream, handler);
		return handler.getLandmarkItems();
	}

	public List<LandMarkItem> getLandmarkItems() {
		return list;
	}

	@Override
	public void startDocument() throws SAXException {
		this.list = new ArrayList<LandMarkItem>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if ("landmark".equals(qName)) {
			lmItem = new LandMarkItem();

			lmId = new StringBuffer();
			memId = new StringBuffer();
			lmName = new StringBuffer();
			lmType = new StringBuffer();
			mediaType = new StringBuffer();
			mediaPath = new StringBuffer();
			longtitude = new StringBuffer();
			latitude = new StringBuffer();
		}
		preTag = qName;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (preTag != null) {
			String content = new String(ch, start, length);
			if ("landmarkid".equals(preTag)) {
				lmId.append(Html.fromHtml(content));
			} else if ("memoryid".equals(preTag)) {
				memId.append(Html.fromHtml(content));
			} else if ("landmarkname".equals(preTag)) {
				lmName.append(Html.fromHtml(content));
			} else if ("landmarktype".equals(preTag)) {
				lmType.append(Html.fromHtml(content));
			} else if ("mediatype".equals(preTag)) {
				mediaType.append(Html.fromHtml(content));
			} else if ("mediapath".equals(preTag)) {
				mediaPath.append(Html.fromHtml(content));
			} else if ("longtitude".equals(preTag)) {
				longtitude.append(Html.fromHtml(content));
			} else if ("latitude".equals(preTag)) {
				latitude.append(Html.fromHtml(content));
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if ("landmark".equals(qName)) {
			lmItem.setLandmarkId(lmId.toString());
			lmItem.setMemoryId(memId.toString());
			lmItem.setLmName(lmName.toString());
			lmItem.setLmType(lmType.toString());
			lmItem.setMediaType(mediaType.toString());
			lmItem.setMediaPath(mediaPath.toString());
			lmItem.setLatitude(latitude.toString());
			lmItem.setLongtitude(longtitude.toString());

			list.add(lmItem);

			lmItem = null;

			lmId = null;
			memId = null;
			lmName = null;
			lmType = null;
			mediaPath = null;
			mediaType = null;
			latitude = null;
			longtitude = null;
		}
		preTag = null;
	}
}
