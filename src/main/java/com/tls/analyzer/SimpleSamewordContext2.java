package com.tls.analyzer;

import java.util.HashMap;
import java.util.Map;

public class SimpleSamewordContext2 implements SamewordContext {
	
	Map<String,String[]> maps = new HashMap<String,String[]>();
	public SimpleSamewordContext2() {
		maps.put("中国",new String[]{"天朝","大陆"});
	}

	public String[] getSamewords(String name) {
		return maps.get(name);
	}

}
