package commonsense;

import java.util.Set;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
 
import javax.net.ssl.HttpsURLConnection;

public class FreeBaseCaller {
	
	/*
	 * Looks up the set of identities that freebase ascribes to a given entity
	 * @TODO: free base API console is DOWN
	 * 
	 * @return
	 */
	public static Set<String> lookup(String entity) throws Exception {
		String url = "http://www.googleapis.com/freebase/v1sandbox/search?query=" + entity;
		URL urlObject = new URL(url);
		HttpURLConnection connection = (HttpURLConnection)urlObject.openConnection();
		
		connection.setRequestMethod("GET");
		
		// add request handler
		//connection.setRequestProperty("User-Agent", USER_AGENT);
		
		int responseCode = connection.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		//print result
		System.out.println(response.toString());
		
		return null;
	}
}