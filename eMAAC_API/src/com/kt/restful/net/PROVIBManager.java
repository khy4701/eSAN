package com.kt.restful.net;

import java.util.*;


public class PROVIBManager implements Receiver{
	private static PROVIBManager provManager;
	
	private static ArrayList<SenderInfo> provMembers;
	
	private PROVIBManager() {
		provMembers = new ArrayList<SenderInfo>();
	}
	
	public static PROVIBManager getInstance() {
		if(provManager == null) {
			provManager = new PROVIBManager();
		}
		
		return provManager;
	}
	
	private int clientReqID = 0;
	
	public synchronized int getClientReqID(){
		clientReqID++;
		if(clientReqID > 2000000000){
			clientReqID = 0;
		}
		
		return clientReqID;
	}

//	public synchronized static void sendCommand(String command, List<String[]> params, Listener source, int reqId, String akey) {
	public synchronized static void sendCommand(String command, String params, Listener source, int reqId, String akey) {

		SenderInfo sender = new SenderInfo(reqId, source);
		provMembers.add(sender);
		
		if(!PROVIBConnector.getInstance().sendMessage(command, params, reqId, akey)) {			
			provMembers.remove(sender);
		}
	}

	// [Client Mode] Send to response
	public synchronized static void sendCommand(String command, int resCode, int reqId) {
		
		PROVIBConnector.getInstance().sendMessage(command, resCode , reqId);			
	}
	
	public synchronized void receiveMessage(String message, int rspCode, int cliReqId) {
		SenderInfo sender = null;
		
		for(int i = 0 ; i < provMembers.size() ; i++) {
			sender = (SenderInfo)provMembers.get(i);
			if(sender != null) {
				if(sender.getCliReqId() == cliReqId) {
					Listener dbmListener = sender.getSource();
					dbmListener.setComplete(message, rspCode, cliReqId);
					provMembers.remove(i);
				}
			}
		}
	}
}
