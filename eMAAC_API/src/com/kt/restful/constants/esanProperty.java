package com.kt.restful.constants;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class esanProperty {
	private static Map<String, String> propHandlerMap = new HashMap<String, String>();
	static {
		Properties prop = new Properties();
		InputStream fis = null;
		try {
			fis =  esanProperty.class.getClassLoader().getResourceAsStream("/esan.properties");
			prop.load(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {try { fis.close(); } catch (IOException ex) { } }
		}
		
		Iterator<Object> keyIter = prop.keySet().iterator();
		while (keyIter.hasNext()) {
			String constName = (String) keyIter.next();
			String path = prop.getProperty(constName);
			propHandlerMap.put(constName, path);
		}
	}

	private esanProperty(){}
	
	public static String getPropPath(String constName) {
		return propHandlerMap.get(constName);
	}
}
