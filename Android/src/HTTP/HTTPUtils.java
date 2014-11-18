package HTTP;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.livefeed.Objects.ForwardData;

/**
 * A tool class to allow posting Json files on a web service using HTTP Post.
 * @author Thomas Rouvinez
 * @creation date: 2014.11.13
 * @last modified: 2014.11.13
 * 
 */
public class HTTPUtils {

	private static HttpClient httpClient = new DefaultHttpClient();
	private static HttpResponse response;

	public static HttpResponse sendJson(String postAddress, ForwardData data){
		try {
			// Create HTTP objects.
			HttpPost httpPost = new HttpPost(postAddress);
			httpPost.setHeader("content-type", "application/json");
			response = null;

			// Json conversion.
			ObjectMapper mapper = new ObjectMapper();
			String jsonValue = mapper.writeValueAsString(data);

			// Convert to String.
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
