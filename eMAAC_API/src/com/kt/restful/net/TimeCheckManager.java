package com.kt.restful.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.kt.restful.model.CheckInfo;

public class TimeCheckManager {
	private static TimeCheckManager tsManager;

	private static ArrayList<CheckInfo> checkMembers;

	private TimeCheckManager() {
		checkMembers = new ArrayList<CheckInfo>();
	}

	public static TimeCheckManager getInstance() {
		if (tsManager == null) {
			tsManager = new TimeCheckManager();
		}
		return tsManager;
	}

	// Check Time per seconds.
	// If time value over 10 seconds, then send Notify Message to Other Process.
	public synchronized static void checkOverTime() {

		CheckInfo chkInfo;
		long old, now;
		
		for (int i = 0; i < checkMembers.size(); i++) {
			chkInfo = checkMembers.get(i);
			if (chkInfo != null) {
				
				old = chkInfo.getOldTime();
				now = Calendar.getInstance().getTimeInMillis();
				
				if ( now - old < 10 * 1000 )
					continue;

				// Send to Other Process.
				System.out.println("Overtime!! : " + chkInfo.getSource());
				
				checkMembers.remove(i);
			}
		}
	}

	public synchronized static void setCheckInfo(String source) {
		
		System.out.println("Set Check Info.. ");
		
		CheckInfo sender = new CheckInfo(source);
		checkMembers.add(sender);
	}	
}
