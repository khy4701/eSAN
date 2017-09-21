package com.kt.restful.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Encoded;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonProperty;
import org.json.JSONObject;

import com.kt.restful.model.ApiDefine;
import com.kt.restful.net.Listener;
import com.kt.restful.net.PLTEConnector;
import com.kt.restful.net.PLTEManager;
import com.kt.restful.net.PROVIBManager;

@Path("/query/Device-subscriber")
public class deviceSubsService implements Listener {
	
	private static Logger logger = LogManager.getLogger(deviceSubsService.class);

	
	@SuppressWarnings("static-access")
	@POST
	@Produces("application/json;charset=UTF-8")
	//@Consumes("application/x-www-form-urlencoded")
	public Response getTokenServ(@Context HttpServletRequest req, @HeaderParam("akey")  @Encoded String akey, @JsonProperty("") String jsonbody ) {

		// 01. Read Json Parameter
		JSONObject jsonObj = new JSONObject(jsonbody);
		String jsonBody = jsonObj.toString();

//		String nasMdn = jsonObj.get("NAS_MDN").toString();
//		String mac = null;
//		String ip  = null; 
//
//		try {
//			mac = jsonObj.get("MAC").toString();
//		} catch (Exception e) {
//			mac = null;
//		}
//
//		try {
//			ip = jsonObj.get("IP").toString();
//		} catch (Exception e) {
//			ip = null;
//		}
		
		// 00. Logging Receive Message 
		if(PLTEConnector.getInstance().isLogFlag()) {
			logger.info("=============================================");
			logger.info(ApiDefine.DEV_SUBS.getName());
			logger.info("REQUEST URL : " + req.getRequestURL().toString());
			logger.info("akey : " + akey);
			logger.info("BODY : " + jsonBody);
//			logger.info("NAS_MDN : " + nasMdn);
//			logger.info("MAC : " + mac);
//			logger.info("IP : " + ip);
			logger.info("=============================================");
		}
						
		// 01. Message Setting and Send ( APP -> PLTEIB )
		int clientID = PLTEManager.getInstance().getClientReqID();
		
//		List<String[]> params = new ArrayList<String[]>();
//
//		if(nasMdn != null) {
//			String[] param1 = {"NAS_MDN", nasMdn};
//			params.add(param1);
//		}
//		
//		if(mac != null) {
//			String[] param2 = {"MAC", mac};
//			params.add(param2);
//		}
//		
//		if(ip != null) {
//			String[] param3 = {"IP", ip};
//			params.add(param3);
//		}
//
	
		PROVIBManager.getInstance().sendCommand(ApiDefine.DEV_SUBS.getName(), jsonBody, this, clientID, akey);
		
	    int resultCode = 200;
		// 02. Waiting 
		while(clientID != receiveReqID ){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} 		  	
		
		switch (rspCode) {
		case 1:
			resultCode = 200;
			break;
		case -1:
			resultCode = 400;
			break;
		case -2:
			resultCode = 404;
			break;
		case -4:
			resultCode = 503;
			break;
		default :
			resultCode = 500;				
		}
		
		// [TEMP]
//		rspCode = 200;		
//		JSONObject responseJSONObject = new JSONObject();
//				
//		responseJSONObject.put("code", 100);
//		responseJSONObject.put("message", "success");
//		this.msg = responseJSONObject.toString();
//		resultCode = rspCode;
		// [/TEMP]
		
		// Add Timer to Check 10 seconds
		//TimeCheckManager.getInstance().setCheckInfo(this.msg);
		
		
		// 03. Setting and Send to APP ( PLTEIB -> APP )
		if(PLTEConnector.getInstance().isLogFlag()) {
			logger.info("=============================================");
			logger.info(ApiDefine.DEV_SUBS.getName() + " REPONSE");
			logger.info("Stauts : " + resultCode);
			logger.info(this.msg);
			logger.info("=============================================");
		}

		return Response.status(resultCode).entity(this.msg).build();
	}
	
	private int receiveReqID = -1;
	private int rspCode = -1;
	private String msg = "";

	@Override
	public void setComplete(String msg, int rspCode, int reqId) {
		// TODO Auto-generated method stub
		this.msg = msg;
		this.rspCode = rspCode;
		this.receiveReqID = reqId;

	}

}
