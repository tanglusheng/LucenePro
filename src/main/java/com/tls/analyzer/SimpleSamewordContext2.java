package com.tls.analyzer;

import java.util.HashMap;
import java.util.Map;

public class SimpleSamewordContext2 implements SamewordContext {
	
	Map<String,String[]> maps = new HashMap<String,String[]>();
	public SimpleSamewordContext2() {
		maps.put("�й�",new String[]{"�쳯","��½"});
	}

	public String[] getSamewords(String name) {
		return maps.get(name);
	}

}
