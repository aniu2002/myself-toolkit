package com.sparrow.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http.multipart.*;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.EndOfDataDecoderException;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by Administrator on 2016/11/14.
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

    private FullHttpRequest fullHttpRequest;

    private boolean readingChunks;

    private final StringBuilder responseContent = new StringBuilder();

    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); //Disk

    private ServerCookieDecoder cookieDecoder = ServerCookieDecoder.LAX;
    private ServerCookieEncoder cookieEncoder = ServerCookieEncoder.LAX;

    private HttpPostRequestDecoder decoder;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (decoder != null) {
            decoder.cleanFiles();
        }
    }

    public void messageReceived(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        if (!msg.decoderResult().isSuccess()) {
            sendError(ctx, BAD_REQUEST);
            return;
        }
        fullHttpRequest =   msg;
        if (logger.isDebugEnabled())
            logger.debug("request : {} {}  handler:{}", new Object[]{fullHttpRequest.method().name(), fullHttpRequest.uri(), this});
        if (HttpServer.isSSL) {
            if (logger.isDebugEnabled())
                logger.debug("Your session is protected by {} ,cipher suite.\n", ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite());
        }
        /**
         * 在服务器端打印请求信息
         */
        System.out.println("VERSION: " + fullHttpRequest.protocolVersion().text() + "\r\n");
        System.out.println("REQUEST_URI: " + fullHttpRequest.uri() + "\r\n\r\n");
        System.out.println("\r\n\r\n");
//        for (Map.Entry<String, String> entry : fullHttpRequest.headers()) {
//            System.out.println("HEADER: " + entry.getKey() + '=' + entry.getValue() + "\r\n");
//        }

        /**
         * 服务器端返回信息
         */
        responseContent.setLength(0);
        responseContent.append("WELCOME TO THE WILD WILD WEB SERVER\r\n");
        responseContent.append("===================================\r\n");

        responseContent.append("VERSION: " + fullHttpRequest.protocolVersion().text() + "\r\n");
        responseContent.append("REQUEST_URI: " + fullHttpRequest.uri() + "\r\n\r\n");
        responseContent.append("\r\n\r\n");
        for (Map.Entry<String, String> entry : fullHttpRequest.headers()) {
            responseContent.append("HEADER: " + entry.getKey() + '=' + entry.getValue() + "\r\n");
        }
        responseContent.append("\r\n\r\n");
        Set<Cookie> cookies;
        String value = fullHttpRequest.headers().get(COOKIE);
        if (value == null) {
            cookies = Collections.emptySet();
        } else {
            cookies = cookieDecoder.decode(value);
        }
        for (Cookie cookie : cookies) {
            responseContent.append("COOKIE: " + cookie.toString() + "\r\n");
        }
        responseContent.append("\r\n\r\n");

        if (fullHttpRequest.method().equals(HttpMethod.GET)) {
            //get请求
            QueryStringDecoder decoderQuery = new QueryStringDecoder(fullHttpRequest.getUri());
            Map<String, List<String>> uriAttributes = decoderQuery.parameters();
            for (Map.Entry<String, List<String>> attr : uriAttributes.entrySet()) {
                for (String attrVal : attr.getValue()) {
                    responseContent.append("URI: " + attr.getKey() + '=' + attrVal + "\r\n");
                }
            }
            responseContent.append("\r\n\r\n");

            responseContent.append("\r\n\r\nEND OF GET CONTENT\r\n");
            writeResponse(ctx.channel());
            return;
        } else if (fullHttpRequest.method().equals(HttpMethod.POST)) {
            //post请求
            decoder = new HttpPostRequestDecoder(factory, fullHttpRequest);
            readingChunks = HttpUtil.isTransferEncodingChunked(fullHttpRequest);
            responseContent.append("Is Chunked: " + readingChunks + "\r\n");
            responseContent.append("IsMultipart: " + decoder.isMultipart() + "\r\n");
            try {
                while (decoder.hasNext()) {
                    InterfaceHttpData data = decoder.next();
                    if (data != null) {
                        try {
                            writeHttpData(data);
                        } finally {
                            data.release();
                        }
                    }
                }
            } catch (EndOfDataDecoderException e1) {
                responseContent.append("\r\n\r\nEND OF POST CONTENT\r\n\r\n");
            }
            writeResponse(ctx.channel());
            return;
        } else {
            System.out.println("discard.......");
            return;
        }
    }

    private void reset() {
        fullHttpRequest = null;
        // destroy the decoder to release all resources
        decoder.destroy();
        decoder = null;
    }

    private void writeHttpData(InterfaceHttpData data) {

        /**
         * HttpDataType有三种类型
         * Attribute, FileUpload, InternalAttribute
         */
        if (data.getHttpDataType() == HttpDataType.Attribute) {
            Attribute attribute = (Attribute) data;
            String value;
            try {
                value = attribute.getValue();
            } catch (IOException e1) {
                e1.printStackTrace();
                responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ":"
                        + attribute.getName() + " Error while reading value: " + e1.getMessage() + "\r\n");
                return;
            }
            if (value.length() > 100) {
                responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ":"
                        + attribute.getName() + " data too long\r\n");
            } else {
                responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ":"
                        + attribute.toString() + "\r\n");
            }
        }
    }


    /**
     * http返回响应数据
     *
     * @param channel
     */
    private void writeResponse(Channel channel) {
        // Convert the response content to a ChannelBuffer.
        ByteBuf buf = copiedBuffer(responseContent.toString(), CharsetUtil.UTF_8);
        responseContent.setLength(0);

        // Decide whether to close the connection or not.
        boolean close = fullHttpRequest.headers().contains(CONNECTION, HttpHeaderValues.CLOSE, true)
                || fullHttpRequest.protocolVersion().equals(HttpVersion.HTTP_1_0)
                && !fullHttpRequest.headers().contains(CONNECTION, HttpHeaderValues.KEEP_ALIVE, true);

        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, buf);
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

        if (!close) {
            // There's no need to add 'Content-Length' header
            // if this is the last response.
            response.headers().set(CONTENT_LENGTH, buf.readableBytes());
        }

        Set<Cookie> cookies;
        String value = fullHttpRequest.headers().get(COOKIE);
        if (value == null) {
            cookies = Collections.emptySet();
        } else {
            cookies = cookieDecoder.decode(value);
        }
        if (!cookies.isEmpty()) {
            // Reset the cookies if necessary.
            for (Cookie cookie : cookies) {
                response.headers().add(SET_COOKIE, cookieEncoder.encode(cookie));
            }
        }
        // Write the response.
        ChannelFuture future = channel.writeAndFlush(response);
        // Close the connection after the write operation is done if necessary.
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
        //   QueryStringDecoder decoderQuery;
    }

    private static void sendError(ChannelHandlerContext ctx,
                                  HttpResponseStatus status) {
        String ret =  null;
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                status, Unpooled.copiedBuffer(ret, CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/xml; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(responseContent.toString(), cause);
        cause.printStackTrace();
        ctx.channel().close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        messageReceived(ctx, msg);
    }
}
