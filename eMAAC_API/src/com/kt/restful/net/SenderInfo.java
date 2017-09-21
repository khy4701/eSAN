package com.kt.restful.net;

public class SenderInfo {
	private int cliReqId;
	private Listener source;

	public SenderInfo(int cliReqId, Listener source) {
		this.cliReqId = cliReqId;
		this.source = source;
	}

	public int getCliReqId() {
		return this.cliReqId;
	}

	public Listener getSource() {
		return this.source;
	}

//	public void display() {
//	    System.out.println("cliReqId= " + cliReqId + "source= " + source 
//	            + "key= " + key + "index= " + index);
//	}
}