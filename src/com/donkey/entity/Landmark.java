package com.donkey.entity;

public class Landmark {
	
	private int landmarkId;
	private String landmarkName;
	private int landmarkType;
	private String landmarkContent;
	private int mediaType;
	private String mediaPath;
	private float longitude;
	private float latitude;
	private int memId;

	public int getMemId() {
		return memId;
	}

	public void setMemId(int memId) {
		this.memId = memId;
	}

	public int getLandmarkId() {
		return landmarkId;
	}

	public void setLandmarkId(int landmarkId) {
		this.landmarkId = landmarkId;
	}

	public String getLandmarkName() {
		return landmarkName;
	}

	public void setLandmarkName(String landmarkName) {
		this.landmarkName = landmarkName;
	}

	public int getLandmarkType() {
		return landmarkType;
	}

	public void setLandmarkType(int landmarkType) {
		this.landmarkType = landmarkType;
	}

	public String getLandmarkContent() {
		return landmarkContent;
	}

	public void setLandmarkContent(String landmarkContent) {
		this.landmarkContent = landmarkContent;
	}

	public int getMediaType() {
		return mediaType;
	}

	public void setMediaType(int mediaType) {
		this.mediaType = mediaType;
	}

	public String getMediaPath() {
		return mediaPath;
	}

	public void setMediaPath(String mediaPath) {
		this.mediaPath = mediaPath;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

}
