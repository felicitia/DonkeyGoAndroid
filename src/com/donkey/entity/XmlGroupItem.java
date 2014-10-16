package com.donkey.entity;

import java.io.Serializable;

public class XmlGroupItem implements Serializable{

	public String getGonline() {
		return gonline;
	}
	public void setGonline(String gonline) {
		this.gonline = gonline;
	}
	public String getGid() {
		return gid;
	}
	public void setGid(String gid) {
		this.gid = gid;
	}
	public String getGname() {
		return gname;
	}
	public void setGname(String gname) {
		this.gname = gname;
	}
	public String getGnum() {
		return gnum;
	}
	public void setGnum(String gnum) {
		this.gnum = gnum;
	}
	
	
	private static final long serialVersionUID = 1L;
	
	private String gid;
	private String gname;
	private String gnum;
	private String gonline;
	
}
