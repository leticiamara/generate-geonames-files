package br.com.leticiamara.generatejsonfilesgeonames;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConection {
	
	public static String makeRequest(String requestURL) {
	    URL url;
	    HttpURLConnection request;
	    String responseData = "";
		try {
			url = new URL(requestURL);
			request = (HttpURLConnection) url.openConnection();
			request.connect();
			responseData =  getStringFromInputStream(request.getInputStream());
			System.out.println("Http Respose Data: " + responseData);
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			ioe.printStackTrace();
		} 
		return responseData;
	}
	
	public static String getStringFromInputStream(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
}
