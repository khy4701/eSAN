package com.kt.restful.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Encoded;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.kt.restful.model.ApiDefine;
import com.kt.restful.net.Listener;
import com.kt.restful.net.PLTEConnector;
import com.kt.restful.net.PLTEManager;

@Path("/app/plteact")
public class plteActService implements Listener {
	
	private static Logger logger = LogManager.getLogger(plteActService.class);

	
	@SuppressWarnings("static-access")
	@POST
	@Produces("application/json;charset=UTF-8")
	//@Consumes("application/x-www-form-urlencoded")
	public Response plteActServ(@Context HttpServletRequest req, 
			@QueryParam("mdn") String mdn  , @QueryParam("authKey") @Encoded String authKey, 
			@QueryParam("plteMode") String plteMode, @QueryParam("imsi") String imsi,
			@QueryParam("osType") String osType, @QueryParam("deviceName") String deviceName ) {

		// 00. Logging Receive Message 
		if(PLTEConnector.getInstance().isLogFlag()) {
			logger.info("=============================================");
			logger.info(ApiDefine.PLTE_ACT.getName());
			logger.info("REQUEST URL : " + req.getRequestURL().toString());
			logger.info("MDN : " + mdn);
			logger.info("authKey : " + authKey);
			logger.info("plteMode : " + plteMode);
			logger.info("imsi : " + imsi);
			logger.info("osType : " + osType);
			logger.info("deviceName : " + deviceName);
			logger.info("=============================================");
		}
		
		// 01. Message Setting and Send ( APP -> PLTEIB )
		int clientID = PLTEManager.getInstance().getClientReqID();
		
		List<String[]> params = new ArrayList<String[]>();

		if(mdn != null) {
			String[] param1 = {"mdn", mdn};
			params.add(param1);
		}
		
		if(authKey != null) {
			String[] param2 = {"authKey", authKey};
			params.add(param2);
		}
		
		if(plteMode != null) {
			String[] param3 = {"plteMode", plteMode};
			params.add(param3);
		}
		
		if(imsi != null) {
			String[] param4 = {"imsi", imsi};
			params.add(param4);
		}
		
		if(osType != null) {
			String[] param5 = {"osType", osType};
			params.add(param5);
		}
		
		if(deviceName != null) {
			String[] param6 = {"deviceName", deviceName};
			params.add(param6);
		}
	
		PLTEManager.getInstance().sendCommand(ApiDefine.PLTE_ACT.getName(), params, this, clientID, "NOT USED");
		
	    int resultCode = 200;
		// 02. Waiting 
		while(clientID != receiveReqID ){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} 		  	
		
		if(rspCode == 1) {
			resultCode = 200;
		} 
		else if (rspCode == 0){
			resultCode = 500;
		}
				
		// 03. Setting and Send to APP ( PLTEIB -> APP )
		if(PLTEConnector.getInstance().isLogFlag()) {
			logger.info("=============================================");
			logger.info(ApiDefine.PLTE_ACT.getName() + " REPONSE");
			logger.info("Stauts : " + resultCode);
			logger.info(this.msg);
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
