package com.donkey.entity;

import java.io.Serializable;

public class XmlGroupDetail extends XmlMemoryItem implements Serializable {

	public String getFriendId() {
		return friendId;
	}

	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	
	private static final long serialVersionUID = 1L;
	
	private String state;
	private String friendId;
	
}
