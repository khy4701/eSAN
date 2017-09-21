package com.kt.restful.net;

import java.util.*;


public class PLTEManager implements Receiver{
	private static PLTEManager plteManager;
	
	private static ArrayList<SenderInfo> plteMembers;
	
	private PLTEManager() {
		plteMembers = new ArrayList<SenderInfo>();
	}
	
	public static PLTEManager getInstance() {
		if(plteManager == null) {
			plteManager = new PLTEManager();
		}
		
		return plteManager;
	}
	
	private int clientReqID = 0;
	
	public synchronized int getClientReqID(){
		clientReqID++;
		if(clientReqID > 2000000000){
			clientReqID = 0;
		}
		
		return clientReqID;
	}

	public synchronized static void sendCommand(String command, List<String[]> params, Listener source, int reqId, String akey) {

		SenderInfo sender = new SenderInfo(reqId, source);
		plteMembers.add(sender);
		
		if(!PLTEConnector.getInstance().sendMessage(command, params, reqId, akey)) {			
			plteMembers.remove(sender);
		}
	}

	// [Client Mode] Send to response
	public synchronized static void sendCommand(String command, int resCode, int reqId) {
		
		PLTEConnector.getInstance().sendMessage(command, resCode , reqId);			
	}
	
	public synchronized void receiveMessage(String message, int rspCode, int cliReqId) {
		SenderInfo sender = null;
		
		for(int i = 0 ; i < plteMembers.size() ; i++) {
			sender = (SenderInfo)plteMembers.get(i);
			if(sender != null) {
				if(sender.getCliReqId() == cliReqId) {
					Listener dbmListener = sender.getSource();
					dbmListener.setComplete(message, rspCode, cliReqId);
					plteMembers.remove(i);
				}
			}
		}
	}
}
