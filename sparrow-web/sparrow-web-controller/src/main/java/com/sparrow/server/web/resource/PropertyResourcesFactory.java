package com.sparrow.server.web.resource;

public class PropertyResourcesFactory extends ResourcesFactory {
	public MessageResource createResources(String config) {
//		info("Create the resources from conf[" + conf + "] \n\t with the factory '"
//				+ PropertyResourcesFactory.class.getName() + "'");
		return new PropertyResources(this, config, this.returnNull);
	}
}
