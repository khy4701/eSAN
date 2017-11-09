package com.kt.restful.service;

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

@Path("/query/nas")
public class nasInfoService implements Listener {
	
	private static Logger logger = LogManager.getLogger(nasInfoService.class);

	
	@SuppressWarnings("static-access")
	@POST
	@Produces("application/json;charset=UTF-8")
	public Response getNasInfo(@Context HttpServletRequest req, @HeaderParam("akey") @Encoded String akey, @JsonProperty("") String jsonbody ) {

		// 01. Read Json Parameter
		JSONObject jsonObj = null;
		String jsonBody = null;
		try{
			jsonObj = new JSONObject(jsonbody);
			jsonBody= jsonObj.toString();
		}		
		catch(Exception e){
			logger.error("Json Parsing Error  : " + jsonbody);
		}
						
		// 00. Logging Receive Message 
		if(PLTEConnector.getInstance().isLogFlag()) {
			logger.info("=============================================");
			logger.info(ApiDefine.NAS_INFORMATION.getName());
			logger.info("REQUEST URL : " + req.getRequestURL().toString());
			logger.info("akey : " + akey);
			logger.info("BODY : " + jsonBody);
			logger.info("=============================================");
		}
		
		// 01. Message Setting and Send ( APP -> PLTEIB )
		int clientID = PLTEManager.getInstance().getClientReqID();
		
	
		PROVIBManager.getInstance().sendCommand(ApiDefine.NAS_INFORMATION.getName(), jsonBody, this, clientID, akey);
		
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
	
		// 03. Setting and Send to APP ( PLTEIB -> APP )
		if(PLTEConnector.getInstance().isLogFlag()) {
			logger.info("=============================================");
			logger.info(ApiDefine.NAS_INFORMATION.getName() + " REPONSE");
			logger.info("Stauts : " + resultCode);
			logger.debug(this.msg);
			logger.info("=============================================");
		}

		return Response.status(resultCode).header("Content-Length", this.msg.getBytes().length).entity(this.msg).build();
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
