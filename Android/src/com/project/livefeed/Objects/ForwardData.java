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
	public ForwardData(int zoneID, String tagIdentifier, double RSSI, Date timestamp){
		this.timestamp = timestamp;
		this.zoneID = zoneID;
		this.tagIdentifier = tagIdentifier;
		this.RSSI = RSSI;
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
}