package com.snmp.server;

public class TestMessageNotice implements ProgressNotice {

	public void messageNotice(String tid, String percent) {
		System.out.println("Task:" + tid + " Percent:" + percent);
	}

}
