package com.kt.restful.model;

import com.google.gson.annotations.SerializedName;

public class ResponseMsg{
	
	private int code;
	private String message;
	
	// 한글로 표시할 경우 --> 이렇게 표현 가능.	
	@SerializedName("data")
	Status data;
		
	public ResponseMsg(int code, String message){
		this.code = code;
		this.message = message;
	}
	
	public ResponseMsg(int code, String message, int compCode, String compName, String plteStatus, String plteStatusCode){
		this.code = code;
		this.message = message;
		
		data = new Status(compCode, compName, plteStatus, plteStatusCode);
	}
	
	public static class Status{
		private int compCode;
		private String compName;
		private String plteStatus;
		private String plteStatusCode;
		
		public Status(int compCode, String compName, String plteStatus, String plteStatusCode ){
			this.compCode = compCode;
			this.compName = compName;
			this.plteStatus = plteStatus;
			this.plteStatusCode = plteStatusCode;
		}

		public int getCompCode() {
			return compCode;
		}

		public String getCompName() {
			return compName;
		}

		public String getPlteStatus() {
			return plteStatus;
		}

		public String getPlteStatusCode() {
			return plteStatusCode;
		}				
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public Status getStatus(){
		return data;
	}
}
