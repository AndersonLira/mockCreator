package com.andersonlira.mockcreator.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.andersonlira.mockcreator.chain.Executor;
import com.andersonlira.mockcreator.config.Config;
import com.andersonlira.mockcreator.config.Sys;
import com.andersonlira.mockcreator.config.VariableNotDefinedException;
import com.andersonlira.mockcreator.log.*;

public class WsdlExecutor implements Executor {
	private Config config = Config.getInstance();
	private Executor next;

	public static String post(String xml,String methodName) throws Exception{

		String serviceUrl = Sys.getVariable(Config.SERVICE_URL);

		String authString = Sys.getVariable(Config.AUTH_STRING);
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

		InputStream is;

		try{
			is = urlConnection.getInputStream();
		}catch(IOException ex){
			is = urlConnection.getErrorStream();
		}
		InputStreamReader isr = new InputStreamReader(is);

		int numCharsRead;
		char[] charArray = new char[1024];
		StringBuffer sb = new StringBuffer();
		while ((numCharsRead = isr.read(charArray)) > 0) {
			sb.append(charArray, 0, numCharsRead);
		}
		String result = sb.toString();

		if(urlConnection.getResponseCode() != 200){
			ServerFaultException ex = new ServerFaultException();
			ex.setInXml(xml);
			ex.setOutXml(result);
			throw ex;
		}

		return result;
	}


	@Override
	public void setNext(Executor next) {
		this.next = next;
	}

	@Override
	public String get(String xml) throws Exception {
		String methodName = XmlHelper.getMethodName(xml);
		String body = XmlHelper.getBody(xml);
        String key = methodName + body.hashCode();
		Logger.info("Read from server: " + key, Color.ANSI_YELLOW);
		try{
			String result = WsdlExecutor.post(xml, methodName);
			return result;
		}catch(VariableNotDefinedException ve){
			throw ve;
		}catch(ServerFaultException se){
            if(config.showErrorServer()){
                Logger.error("Soap In");
                Logger.info(se.getInXml(),Color.ANSI_PURPLE);
                Logger.error("Soap Out");
                Logger.info(se.getOutXml(),Color.ANSI_PURPLE);
            }
			throw new Exception(se.getOutXml());		
		}catch(Exception ex){
			Logger.error("Server error: " + ex.getMessage());
			throw ex;
		}
	}

}