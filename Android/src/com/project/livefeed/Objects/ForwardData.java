package com.project.livefeed.Objects;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author Thomas Rouvinez
 * @creation date: 2014.11.13
 * @last modified: 2014.11.26
 *
 */
public class ForwardData {

	// ---------------------------------------------------------------
	// Variables.
	// ---------------------------------------------------------------
	
	private Date timestamp;
	private String sensorID;
	private String eventID;
	private RecordData values[];
	
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
	public ForwardData(Date timestamp, String eventID, String sensorID, RecordData values[]){
		this.timestamp = timestamp;
		this.setEventID(eventID);
		this.setSensorID(sensorID);
		this.setValues(values);
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

	public String getSensorID() {
		return sensorID;
	}

	public void setSensorID(String sensorID) {
		this.sensorID = sensorID;
	}

	public String getEventID() {
		return eventID;
	}

	public void setEventID(String eventID) {
		this.eventID = eventID;
	}

	public RecordData[] getValues() {
		return values;
	}

	public void setValues(RecordData values[]) {
		this.values = values;
	}
}