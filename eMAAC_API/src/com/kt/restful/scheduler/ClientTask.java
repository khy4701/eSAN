package com.kt.restful.scheduler;

import java.util.TimerTask;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.kt.restful.net.PLTEConnector;
import com.kt.restful.net.PLTEManager;
import com.kt.restful.net.PROVIBConnector;
import com.kt.restful.net.TimeCheckManager;

public class ClientTask extends TimerTask {

	private static Logger logger = LogManager.getLogger(ClientTask.class);
	
    @SuppressWarnings("static-access")
	@Override
    public void run() {
    	
    	TimeCheckManager.getInstance().checkOverTime();
    	PLTEConnector.getInstance();
    	PROVIBConnector.getInstance();
    }
}
