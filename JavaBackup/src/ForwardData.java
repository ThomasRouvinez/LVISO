

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
	
	private String timestamp;
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
	public ForwardData(String eventID, String sensorID, RecordData values[]){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		
		this.timestamp = dateFormat.format(cal.getTime());
		this.setEventID(eventID);
		this.setSensorID(sensorID);
		this.setValues(values);
	}

	// ---------------------------------------------------------------
	// Getter-setters.
	// ---------------------------------------------------------------
	
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
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