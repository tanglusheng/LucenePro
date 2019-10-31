package com.tls.analyzer;

import java.util.HashMap;
import java.util.Map;

public class SimpleSamewordContext implements SamewordContext {
	
	Map<String,String[]> maps = new HashMap<String,String[]>();
	public SimpleSamewordContext() {
		maps.put("�й�",new String[]{"�쳯","��½"});
		maps.put("��",new String[]{"��","��"});
	}

	public String[] getSamewords(String name) {
		return maps.get(name);
	}

}