/**  
 * Project Name:http-server  
 * File Name:DefaultMsgListener.java  
 * Package Name:com.sparrow.core.global  
 * Date:2014-2-21上午10:11:43  
 * Copyright (c) 2014, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.server.context;

import com.sparrow.core.listener.MsgListener;
import com.sparrow.core.utils.StringUtils;

/**
 * ClassName:DefaultMsgListener <br/>
 * Date: 2014-2-21 上午10:11:43 <br/>
 * 
 * @author YZC
 * @version
 * @since JDK 1.6
 * @see
 */
public class DefaultMsgListener implements MsgListener {

	@Override
	public void onMessage(String msg) {
		if (StringUtils.isNotEmpty(msg)) {
			msg = msg.replace("\n", "").replace("\r", "").replace('"', '\'');
			//CommandTool.publish("process", "process", msg);
		}
	}

}
