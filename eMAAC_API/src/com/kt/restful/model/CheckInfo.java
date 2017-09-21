package com.kt.restful.model;

import java.util.Calendar;

public class CheckInfo {

	private String source;
	private long timer;
	
	public CheckInfo(String source){
		this.source = source;
		this.timer  = Calendar.getInstance().getTimeInMillis();
	}

	public String getSource() {
		return source;
	}
	
	public long getOldTime(){
		return timer;
	}
	
}
