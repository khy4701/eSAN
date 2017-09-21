package com.kt.restful.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.kt.restful.model.ApiDefine;
import com.kt.restful.model.ProvifMsgType;
import com.kt.restful.net.PLTEConnector;
import com.kt.restful.net.Listener;
import com.kt.restful.net.PLTEManager;

@Path("/app/register_esan")
public class regeSanService implements Listener {
	
	private static Logger logger = LogManager.getLogger(regeSanService.class);

	
	@SuppressWarnings("static-access")
	@POST
	@Produces("application/json;charset=UTF-8")
	//@Consumes("application/x-www-form-urlencoded")
	public Response plteActServ(@Context HttpServletRequest req, 
			@QueryParam("mdn") String mdn  , @QueryParam("osType") String osType, @QueryParam("deviceId") String deviceId) {

		// 00. Logging Receive Message 
		if(PLTEConnector.getInstance().isLogFlag()) {
			logger.info("=============================================");
			logger.info(ApiDefine.PLTE_REG_ESAN.getName());
			logger.info("REQUEST URL : " + req.getRequestURL().toString());
			logger.info("MDN : " + mdn);
			logger.info("osType : " + osType);
			logger.info("deviceId : " + deviceId);
			logger.info("=============================================");
		}
		
		// 01. Message Setting and Send ( APP -> PLTEIB )
		int clientID = PLTEManager.getInstance().getClientReqID();
		
		List<String[]> params = new ArrayList<String[]>();

		if(mdn != null) {
			String[] param1 = {"mdn", mdn};
			params.add(param1);
		}
	
		if(osType != null) {
			String[] param2 = {"osType", osType};
			params.add(param2);
		}
		
		if(deviceId != null) {
			String[] param3 = {"deviceId", deviceId};
			params.add(param3);
		}
			
		PLTEManager.getInstance().sendCommand(ApiDefine.PLTE_REG_ESAN.getName(), params, this, clientID, "NOT USED");
				
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
			logger.info(ApiDefine.PLTE_REG_ESAN.getName() + " REPONSE");
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
