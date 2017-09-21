package com.kt.restful.service;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.kt.restful.constants.esanProperty;
import com.kt.restful.model.ApiDefine;
import com.kt.restful.net.PLTEConnector;
import com.kt.restful.net.PLTEManager;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class pushTriggerService extends Thread {
	
	private int rspCode = -1;
	private String apiName = null;
	private int tid = 0;
	private String mdn = null;
	private String imsi = null;
	private String body = null;
		
	private static Logger logger = LogManager.getLogger(pushTriggerService.class);
	
	public pushTriggerService(String apiName, int tid, String mdn, String imsi, String body ){
		this.apiName = apiName;
		this.mdn = mdn;
		this.imsi = imsi;
		this.body = body;
		this.tid = tid;
	}
	
	@SuppressWarnings("static-access")
	public String sendRequest() {		
		String output = "";
		try {
			Client client = Client.create();
			WebResource webResource = null;
			ClientResponse response = null;
			String url = "";

			url = String.format(ApiDefine.PUSH_TRIGGER.getName(), esanProperty.getPropPath("ext_ipaddress"),
					Integer.parseInt(esanProperty.getPropPath("ext_port")), mdn, imsi  );
					
			if(PLTEConnector.getInstance().isLogFlag()) {
				logger.info("=============================================");
				logger.info("RESTIF -> [EXT_SERVER]");
				logger.info("REQUEST URL : " + url );
				logger.info("=============================================");
			}

			// GET METHOD --> Method 형태에 따라.
			webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class);
			
			if (response.getStatus() == 200) {
				rspCode = 1;
				output = response.getEntity(String.class);
			} else {
				rspCode = 0;
				output = "";
			}
			
			// PLTEIB로 다시 결과 전송해줘야함!! 
			PLTEManager.getInstance().sendCommand(ApiDefine.PUSH_TRIGGER.getName(), rspCode, this.tid);
			
			// 기본 소스 
			//PLTEManager.getInstance().sendCommand(ApiDefine.PLTE_STATUS.getName(), params, this, clientID);
			
			rspCode = response.getStatus();
			if(PLTEConnector.getInstance().isLogFlag()) {
				logger.info("=============================================");
				logger.info("RESTIF -> [EXT_SERVER]");
				logger.info("STATUS : " + response.getStatus() );
				logger.info(output);
				logger.info("=============================================");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			rspCode = -1;
			return "";
		}

		return output;
	}
	
	@SuppressWarnings("static-access")
	@Override
	public synchronized void run() {				
		sendRequest();
	}
		
}
