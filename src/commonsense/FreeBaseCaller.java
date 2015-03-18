package commonsense;

import java.util.HashSet;
import java.util.Set;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
 
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
//import com.jayway.jsonpath.JsonPath;
import java.io.FileInputStream;
import java.util.Properties;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


import javax.net.ssl.HttpsURLConnection;

public class FreeBaseCaller {
	public static Properties properties = new Properties();
	
	public static void main(String[] args) {
		apiQuery("apple");
	}
	
	public static Set<String> apiQuery(String entity) {
		try {
      properties.load(new FileInputStream("freebase.properties"));
      HttpTransport httpTransport = new NetHttpTransport();
      HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
      JSONParser parser = new JSONParser();
      GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/search");
      url.put("query", entity);
      //url.put("filter", "(all type:/music/artist created:\"The Lady Killer\")");
      url.put("limit", "10");
      url.put("indent", "true");
      url.put("key", properties.get("API_KEY"));
      HttpRequest request = requestFactory.buildGetRequest(url);
      HttpResponse httpResponse = request.execute();
      JSONObject response = (JSONObject)parser.parse(httpResponse.parseAsString());
      JSONArray results = (JSONArray)response.get("result");
      
      Set<String> founds = new HashSet<String>();
      for (Object result : results) {
      	System.out.println(result.toString());
      	//founds.add(JsonPath.read(result,"$.name").toString());
      }
      //System.out.println(founds);
      return founds;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
		return null;
	}
	/*
	 * Looks up the set of identities that freebase ascribes to a given entity
	 * 
	 * @return
	 */
	public static Set<String> httpLookup(String entity) {
		try {
			String url = "https://www.googleapis.com/freebase/v1/search?query=" + entity + "&key=<YOUR_API_KEY>";
			URL urlObject = new URL(url);
			HttpURLConnection connection = (HttpURLConnection)urlObject.openConnection();
			
			connection.setRequestMethod("GET");
			
			// add request handler
			//connection.setRequestProperty("User-Agent", USER_AGENT);
			
			int responseCode = connection.getResponseCode();
			//System.out.println("\nSending 'GET' request to URL : " + url);
			//System.out.println("Response Code : " + responseCode);
			if( responseCode / 200 == 2 ) {
				BufferedReader in = new BufferedReader(
		        new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
		 
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				Set<String> result = parseJSON(response.toString());
				in.close();
				return result;
			} else {
				return null;				
			}
		} catch (MalformedURLException e ) {
			e.printStackTrace();
		} catch (IOException i ) {
			i.printStackTrace();
		}
		return null;
	}
	
	private static Set<String> parseJSON(String data) {
		Set<String> dataSet = new HashSet<String>();
		
		return dataSet;
	}
}
