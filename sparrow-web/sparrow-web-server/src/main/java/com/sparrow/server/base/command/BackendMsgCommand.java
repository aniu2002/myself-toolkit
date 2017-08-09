package com.sparrow.server.base.command;

import com.sparrow.common.backend.ProcessMessage;
import com.sparrow.common.backend.ProcessMsgManager;
import com.sparrow.http.command.BaseCommand;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.FreeMarkerResponse;
import com.sparrow.http.command.resp.JsonResponse;
import com.sparrow.http.command.resp.OkResponse;

public class BackendMsgCommand extends BaseCommand {

	protected Response doGet(Request request) {
		return new FreeMarkerResponse("system/message_proc", request.getParas());
	}

	protected Response doPost(Request request) {
		String sid = request.get("_token");
		ProcessMessage msg = ProcessMsgManager.getMessage(sid);
		if (msg != null)
			return new JsonResponse(msg.fetchData());
		return OkResponse.OK;
	}

}
