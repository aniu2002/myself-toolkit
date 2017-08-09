package com.au.flvserver;

public class MiniFlvServer   {

	public boolean isSyn() {
		return true;
	}

	public String getDesc() {
		return "Flv流媒体服务器";
	}

	public boolean serializedEnable() {
		return true;
	}


	public void postStart( ) {
			new com.milgra.server.Server();
	}

}
