package com.sparrow.server.web.aware;

import com.sparrow.server.web.resource.MessageResource;

public class ResourcesAware {
	private MessageResource resource;

	public MessageResource getResources() {
		return this.resource;
	}

	public void setResources(MessageResource resources) {
		this.resource = resources;
	}
}
