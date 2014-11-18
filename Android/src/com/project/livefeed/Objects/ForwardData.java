package com.project.livefeed.Objects;

import java.util.Date;

/**
 * 
 * @author Thomas Rouvinez
 * @creation date: 2014.11.13
 * @last modified: 2014.11.13
 *
 */
public class ForwardData {

	// ---------------------------------------------------------------
	// Variables.
	// ---------------------------------------------------------------
	
	private Date timestamp;
	private int zoneID;
	private String tagIdentifier;
	private double RSSI;
	private double xCoordinate;
	private double yCoordinate;
	private double radius;
	
	// ---------------------------------------------------------------
	// Constructor.
	// ---------------------------------------------------------------
	
	/**
	 * Default Constructor.
	 */
	public ForwardData(){};
	
	/**
	 * Full Constructor.
	 */
	public ForwardData(ZoneInfo zoneInfo, String tagIdentifier, double RSSI, Date timestamp){
		this.timestamp = timestamp;
		this.zoneID = zoneInfo.getZoneID();
		this.tagIdentifier = tagIdentifier;
		this.RSSI = RSSI;
		this.xCoordinate = zoneInfo.getX();
		this.yCoordinate = zoneInfo.getY();
		this.radius = zoneInfo.getRadius();
	}

	// ---------------------------------------------------------------
	// Getter-setters.
	// ---------------------------------------------------------------
	
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public int getZoneID() {
		return zoneID;
	}

	public void setZoneID(int zoneID) {
		this.zoneID = zoneID;
	}

	public String getTagIdentifier() {
		return tagIdentifier;
	}

	public void setTagIdentifier(String tagIdentifier) {
		this.tagIdentifier = tagIdentifier;
	}

	public double getRSSI() {
		return RSSI;
	}

	public void setRSSI(double rSSI) {
		RSSI = rSSI;
	}

	public double getxCoordinate() {
		return xCoordinate;
	}

	public void setxCoordinate(int xCoordinate) {
		this.xCoordinate = xCoordinate;
	}

	public double getyCoordinate() {
		return yCoordinate;
	}

	public void setyCoordinate(int yCoordinate) {
		this.yCoordinate = yCoordinate;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}
}