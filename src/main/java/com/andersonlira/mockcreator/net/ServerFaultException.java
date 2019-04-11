package com.andersonlira.mockcreator.net;

public class ServerFaultException extends Exception {
	
	private String inXml;
	private String outXml;

	public ServerFaultException(){
		super("Server Fault");
	}

	public void setInXml(String inXml){
		this.inXml = inXml;
	}

	public String getInXml(){
		return this.inXml;
	}

	public void setOutXml(String outXml){
		this.outXml = outXml;
	}

	public String getOutXml(){
		return this.outXml;
	}

}