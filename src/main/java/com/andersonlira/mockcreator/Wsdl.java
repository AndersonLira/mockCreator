package com.andersonlira.mockcreator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;

public class Wsdl {

	public static String post(String xml,String methodName) throws Exception{

		String serviceUrl = Sys.getVariable("SERVICE_URL");

		String authString = Sys.getVariable("AUTH_STRING");
		byte[] authEncBytes = authString.getBytes();
		String authStringEnc = new String(authEncBytes);

		URL url = new URL(serviceUrl);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
		urlConnection.setRequestMethod("POST");
		urlConnection.setRequestProperty( "Content-Type", "text/xml; charset=utf-8");
		urlConnection.setDoOutput(true);
		urlConnection.setRequestProperty("SOAPAction", serviceUrl +"#"+methodName);
		urlConnection.getOutputStream().write(xml.getBytes("UTF-8"));

		if(urlConnection.getResponseCode() != 200){
			throw new Exception("Server Fault");
		}
		InputStream is = urlConnection.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);

		int numCharsRead;
		char[] charArray = new char[1024];
		StringBuffer sb = new StringBuffer();
		while ((numCharsRead = isr.read(charArray)) > 0) {
			sb.append(charArray, 0, numCharsRead);
		}
		String result = sb.toString();

		return result;
	}

}