package com.project.livefeed.Objects;

/**
 * 
 * @author Thomas Rouvinez
 * @creation date: 2014.11.26
 * @last modified: 2014.11.26
 *
 */
public class RecordData {

	// ---------------------------------------------------------------
	// Variables.
	// ---------------------------------------------------------------
	
	private String tag;
	private double rssi;
	
	// ---------------------------------------------------------------
	// Constructor.
	// ---------------------------------------------------------------
	
	public RecordData(){}
	
	public RecordData(String tag, double rssi){
		this.tag = tag;
		this.rssi = rssi;
	}
	
	// ---------------------------------------------------------------
	// Getters - Setters.
	// ---------------------------------------------------------------
	
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public double getRSSI() {
		return rssi;
	}
	public void setRSSI(double rssi) {
		this.rssi = rssi;
	}
}