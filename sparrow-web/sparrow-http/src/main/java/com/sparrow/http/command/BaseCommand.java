package com.sparrow.http.command;

import com.sparrow.core.utils.StringUtils;
import com.sparrow.http.command.resp.JsonResponse;
import com.sparrow.http.command.resp.Message;
import com.sparrow.http.command.resp.MsgResponse;
import com.sparrow.http.command.resp.OkResponse;

public class BaseCommand implements Command {

	@Override
	public final Response doCommand(Request request) {
		String _m = request.getMethod();

		if ("post".equalsIgnoreCase(request.getMethod())) {
			String n = request.get("_method");
			if (!StringUtils.isEmpty(n))
				_m = n;
		}

		try {
			if ("get".equalsIgnoreCase(_m)) {
				return this.doGet(request);
			} else if ("put".equalsIgnoreCase(_m)) {
				return this.doPut(request);
			} else if ("delete".equalsIgnoreCase(_m)) {
				return this.doDelete(request);
			} else if ("post".equalsIgnoreCase(_m)) {
				return this.doPost(request);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			return new MsgResponse(MsgResponse.FAILURE, e.getMessage());
		}
		return OkResponse.OK;
	}

	protected Response doPost(Request request) {
		return OkResponse.OK;
	}

	protected Response doPut(Request request) {
		return OkResponse.OK;
	}

	protected Response doDelete(Request request) {
		return OkResponse.OK;
	}

	protected Response doGet(Request request) {
		return OkResponse.OK;
	}

	protected Response wrapErrorResponse(Throwable t) {
		return new JsonResponse(new Message(-1, t.getMessage()));
	}
}
