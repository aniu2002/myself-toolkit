package com.au.cache;

public class TestMain {

	public static void main(String args[]) {
		Cache cache = new Cache("test", 2, true, 5, "D:/test/persist");
		cache.initialise();
		Element ele = new Element("test", new String("I love U"));
		cache.put(ele);
		ele = new Element("test1", new String("I love U"));
		cache.put(ele);
		ele = new Element("test2", new String("I love U"));
		cache.put(ele);
		ele = new Element("test3", new String("I love U"));
		cache.put(ele);
		ele = new Element("test4", new String("I love U"));
		cache.put(ele);
		
		ele = cache.get("test1");
		//cache.remove("test3");
		// save data file's index to disk
		cache.flush();
		System.out.println(ele.getObjectKey()+"="+ele.getValue());
		try {
			Thread.sleep(60 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
