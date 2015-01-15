import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.thingmagic.Reader;
import com.thingmagic.ReaderException;
import com.thingmagic.TMConstants;
import com.thingmagic.TagReadData;

public class ReaderFactory extends Thread{

	// Variables.
	private String ipAddress, comPort, eventID, sensorID;
	private int index = 0;
	private long timeDiff = 0;

	// Constructor,
	public ReaderFactory (String comPort, String eventID, String sensorID, int index, String ipAddress, long timediff){
		this.comPort = comPort;
		this.eventID = eventID;
		this.sensorID = sensorID;
		this.index = index;
		this.ipAddress = ipAddress;
		this.timeDiff = timediff;
	}

	// Runnable.
	@Override
	public void run() {
		//Variables.
		int loopingTime = 1000;
		String oldDate = "";

		// Create Reader object, connecting to physical device
		try{
			TagReadData[] tagReads;

			Main.instances[this.index] = Reader.create("tmr:///" + comPort);
			Main.instances[this.index].connect();

			if (Reader.Region.UNSPEC == (Reader.Region)Main.instances[this.index].paramGet("/reader/region/id"))
			{
				Reader.Region[] supportedRegions = (Reader.Region[])Main.instances[this.index].paramGet(TMConstants.TMR_PARAM_REGION_SUPPORTEDREGIONS);
				if (supportedRegions.length < 1){
					throw new Exception("Reader doesn't support any regions");
				}
				else{
					Main.instances[this.index].paramSet("/reader/region/id", supportedRegions[0]);
				}
			}
			
			// Console output.
			System.out.println(new Date().toLocaleString() + "> Reader " + sensorID + " created and listening"); 
			
			// Read tags
			while(Main.threadDone == false){
				tagReads = Main.instances[this.index].read(400);

				if(tagReads.length > 0){
					// Get time.
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(new Date().getTime() - timeDiff);
					String date = TimeUtils.formatDate(calendar);

					if(date != oldDate){
						RecordData values[] = new RecordData[tagReads.length];

						// Pack information.
						for (int i = 0 ; i < tagReads.length ; i++){
							values[i] = new RecordData(tagReads[i].epcString(), tagReads[i].getRssi());
						}

						ForwardData sample = new ForwardData(eventID, sensorID, values);
						sample.setTimestamp(date);

						// JSON Formatting:
						String jsonValue = "{\"eventID\" : " + sample.getEventID() + 
								", \"sensorID\" : " + sample.getSensorID() +
								", \"timeStamp\" : \"" + date + "\" ," +
								" \"records\" : [";

						// Add each record.
						for(int i = 0 ; i < sample.getValues().length ; i++){
							jsonValue += "{\"tag\" : \"" + sample.getValues()[i].getTag() + "\" ," +
									"\"rssi\" : " + sample.getValues()[i].getRSSI() + "}";

							if(i + 1 != sample.getValues().length){
								jsonValue += ", ";
							}
						}

						// Add ending.
						jsonValue += "]}";

						// Send information.
						HttpPost postRequest = new HttpPost("http://" + ipAddress + "/hubnet/irec/");
						postRequest.setHeader("content-type", "application/json");

						StringEntity entity = new StringEntity(jsonValue);
						postRequest.setEntity(entity);

						HttpClient client = new DefaultHttpClient();
						HttpResponse debug = client.execute(postRequest);
						//System.out.println(">" + date + "R" + sensorID + "> posted ");

						oldDate = date;
					}
				}

				// Restart loop.
				Thread.sleep(300);
			}
		} 
		catch (ReaderException re){
			System.out.println(new Date().toLocaleString() + "> Reader " + sensorID + " exception: " + re.getMessage());
		}
		catch (Exception re){
			System.out.println(new Date().toLocaleString() + "> Reader " + sensorID + " exception: " + re.getMessage());
		}
	}
}