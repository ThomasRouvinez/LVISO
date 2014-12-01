package HTTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.project.livefeed.Wizard;
import com.project.livefeed.Objects.ForwardData;

/**
 * A tool class to allow posting Json files on a web service using HTTP Post.
 * @author Thomas Rouvinez
 * @creation date: 2014.11.13
 * @last modified: 2014.11.13
 * 
 */
public class HTTPUtils extends AsyncTask<ForwardData, Void, HttpResponse>{

	@Override
	protected HttpResponse doInBackground(ForwardData... data) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = null;

		try {
			// Create HTTP objects.
			HttpPost httpPost = new HttpPost(Wizard.serverAddress + "/irec/");
			httpPost.setHeader("content-type", "application/json");

			// JSON Formatting:
			String jsonValue = "{\"eventID\" : " + data[0].getEventID() + 
					", \"sensorID\" : " + data[0].getSensorID() +
					", \"timeStamp\" : \"" + data[0].getTimestamp() + "\" ," +
					" \"records\" : [";
			
			// Add each record.
			for(int i = 0 ; i < data[0].getValues().length ; i++){
				jsonValue += "{\"tag\" : \"" + data[0].getValues()[i].getTag() + "\" ," +
						"\"rssi\" : " + data[0].getValues()[i].getRSSI() + "}";
				
				if(i + 1 != data[0].getValues().length){
					jsonValue += ", ";
				}
			}
			
			// Add ending.
			jsonValue += "]}";

			// Convert to HTTP entity.
			StringEntity entity;
			entity = new StringEntity(jsonValue, HTTP.UTF_8);
			httpPost.setEntity(entity);

			// Proceed with the Post.
			response = httpClient.execute(httpPost);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return response;
	}
}