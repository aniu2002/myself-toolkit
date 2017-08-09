/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.fileupload.httpd;

import com.sun.net.httpserver.HttpExchange;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.UploadContext;
import org.apache.commons.fileupload.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

import static java.lang.String.format;

/**
 * <p>Provides access to the request information needed for a request made to
 * an HTTP servlet.</p>
 *
 * @version $Id: ServletRequestContext.java 1455855 2013-03-13 09:58:59Z simonetripodi $
 * @since FileUpload 1.1
 */
public class HttpdRequestContext implements UploadContext {
    public static final String HEADER_NAME_CONTENTTYPE = "Content-Type";
    // ----------------------------------------------------- Instance Variables

    /**
     * The request for which the context is being provided.
     */
    private final HttpExchange request;
    private final String contentType;

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a context for this request.
     *
     * @param request The request to which this context applies.
     */
    public HttpdRequestContext(HttpExchange request) {
        this.request = request;
        this.contentType = request.getRequestHeaders().getFirst(HEADER_NAME_CONTENTTYPE);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Retrieve the character encoding for the request.
     *
     * @return The character encoding for the request.
     */
    public String getCharacterEncoding() {
        String contentTypeHeaderValue = this.contentType;
        if (contentTypeHeaderValue == null) {
            return null;
        }
        return StringUtils.getCharsetFromContentType(contentTypeHeaderValue);
    }

    /**
     * Retrieve the content type of the request.
     *
     * @return The content type of the request.
     */
    public String getContentType() {
        return this.contentType;
    }

    /**
     * Retrieve the content length of the request.
     *
     * @return The content length of the request.
     * @deprecated 1.3 Use {@link #contentLength()} instead
     */
    @Deprecated
    public int getContentLength() {
        return (int) contentLength();
    }

    /**
     * Retrieve the content length of the request.
     *
     * @return The content length of the request.
     * @since 1.3
     */
    public long contentLength() {
        long size;
        try {
            size = Long.parseLong(request.getRequestHeaders().getFirst(FileUploadBase.CONTENT_LENGTH));
        } catch (NumberFormatException e) {
            size = 0;
        }
        return size;
    }

    /**
     * Retrieve the input stream for the request.
     *
     * @return The input stream for the request.
     * @throws java.io.IOException if a problem occurs.
     */
    public InputStream getInputStream() throws IOException {
        return request.getRequestBody();
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object.
     */
    @Override
    public String toString() {
        return format("ContentLength=%s, ContentType=%s",
                this.contentLength(),
                this.getContentType());
    }

}
