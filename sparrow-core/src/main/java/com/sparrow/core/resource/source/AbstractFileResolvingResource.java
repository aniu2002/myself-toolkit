package com.sparrow.core.resource.source;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import com.sparrow.core.utils.ResourceUtils;


 
public abstract class AbstractFileResolvingResource extends AbstractResource {

	@Override
	public File getFile() throws IOException {
		URL url = getURL();
		return ResourceUtils.getFile(url, getDescription());
	}

	protected File getFile(URI uri) throws IOException {
		return ResourceUtils.getFile(uri, getDescription());
	}

}
