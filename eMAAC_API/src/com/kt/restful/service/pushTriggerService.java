package com.kt.restful.service;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.kt.restful.constants.esanProperty;
import com.kt.restful.model.ApiDefine;
import com.kt.restful.model.ProvifMsgType;
import com.kt.restful.net.PLTEConnector;
import com.kt.restful.net.PLTEManager;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class pushTriggerService extends Thread {
	
	private int rspCode = -1;
	private String apiName = null;
	private int tid = 0;
	private String aKey = null;
	private String body = null;
	private String apiHost = null;
	private int apiPort = 0;
		
	private static Logger logger = LogManager.getLogger(pushTriggerService.class);
	
	public pushTriggerService(String apiName, int tid, String body ){

		this.apiName = apiName;
		this.body = body;
		this.tid = tid;
	}
	
	public pushTriggerService(ProvifMsgType pmt) {

		this.apiName = pmt.getApiName();
		this.tid = Integer.parseInt(pmt.getSeqNo());
		this.aKey = pmt.getAuthKey();
		this.apiHost = pmt.getApiHost();
		this.apiPort = pmt.getApiPort();
		this.body = pmt.getData();

	}
	
	
	@SuppressWarnings("static-access")
	public void sendRequest() {		
		String output = "";
		int resCode = 200;
		try {
			Client client = Client.create();
			WebResource webResource = null;
			ClientResponse response = null;
			String url = "";

			url = new String("https://"+this.apiHost+":"+this.apiPort+"/api/query/nas");
			
			if(PLTEConnector.getInstance().isLogFlag()) {
				logger.info("=============================================");
				logger.info("RESTIF -> [API_SERVER]");
				logger.info("tid : " + tid );
				logger.info("URL : " + url );
				logger.info("aKey :" + aKey );
				logger.info("apiName : " + apiName);
				logger.debug("body : " + body);
				logger.info("=============================================");
			}

			// GET METHOD --> Method 형태에 따라.
			try{
				webResource = client.resource(url);
				response = webResource.type(MediaType.APPLICATION_JSON).header("akey", aKey).header("channel", "esan")
						.post(ClientResponse.class, body);
				
				resCode = response.getStatus();
				if (resCode == 200) {
					rspCode = 1;
				} else if(resCode >= 400 && resCode < 600){
					rspCode = 0;				
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
				resCode = 403;
				rspCode = 0;
			}
			
			if(PLTEConnector.getInstance().isLogFlag()) {
				logger.info("=============================================");
				logger.info("[API_SERVER] -> RESTIF");
				logger.info("tid : " + tid );
				logger.info("apiName : " + apiName);
				logger.info("aKey : " + aKey );
				logger.info("body : " + body );
				logger.info("resCode : " + resCode );
				logger.info("=============================================");
			}
			
			// PLTEIB로 다시 결과 전송해줘야함!! 
			PLTEManager.getInstance().sendCommand(apiName, rspCode, tid, aKey);
			
			
			response.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());

		}
	}
	
	@SuppressWarnings("static-access")
	@Override
	public synchronized void run() {				
		sendRequest();
	}
		
}
