package com.sparrow.netty.command.resp;

import com.sparrow.netty.command.Response;

import com.sparrow.core.json.JsonMapper;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import java.io.IOException;

public class JsonResponse implements Response {
	Object data;

	public JsonResponse(Object data) {
		this.data = data;
	}

	public int getStatus() {
		return 200;
	}

	public String toMessage() {
		try {
			return JsonMapper.mapper.writeValueAsString(data);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
