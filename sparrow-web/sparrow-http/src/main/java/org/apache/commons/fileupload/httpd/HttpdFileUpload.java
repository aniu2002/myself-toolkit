package org.apache.commons.fileupload.httpd;

import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.fileupload.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class HttpdFileUpload extends FileUpload {
    private static final String POST_METHOD = "POST";

    public static final boolean isMultipartContent(
            HttpExchange request) {
        if (!POST_METHOD.equalsIgnoreCase(request.getRequestMethod())) {
            return false;
        }
        return FileUploadBase.isMultipartContent(new HttpdRequestContext(request));
    }


    public HttpdFileUpload() {
        super();
    }


    public HttpdFileUpload(FileItemFactory fileItemFactory) {
        super(fileItemFactory);
    }

    public List<FileItem> parseRequest(HttpExchange request)
            throws FileUploadException {
        return parseRequest(new HttpdRequestContext(request));
    }


    public Map<String, List<FileItem>> parseParameterMap(HttpExchange request)
            throws FileUploadException {
        return parseParameterMap(new HttpdRequestContext(request));
    }


    public FileItemIterator getItemIterator(HttpExchange request)
            throws FileUploadException, IOException {
        return super.getItemIterator(new HttpdRequestContext(request));
    }
}
