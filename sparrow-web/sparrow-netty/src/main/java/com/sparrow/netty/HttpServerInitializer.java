package com.sparrow.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import javax.net.ssl.SSLEngine;

/**
 * Created by Administrator on 2016/11/14.
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = socketChannel.pipeline();

        if (HttpServer.isSSL) {
            SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
            engine.setNeedClientAuth(true); //ssl双向认证
            engine.setUseClientMode(false);
            engine.setWantClientAuth(true);
            engine.setEnabledProtocols(new String[]{"SSLv3"});
            pipeline.addLast("ssl", new SslHandler(engine));
        }

        pipeline.addLast("http-read-timeout", new ReadTimeoutHandler(10));
        pipeline.addLast("http-write-timeout", new WriteTimeoutHandler(10));
        /**
         * http-request解码器
         * http服务器端对request解码
         */
      //  pipeline.addLast("http-decoder", new HttpRequestDecoder());
        /**
         * http-response解码器
         * http服务器端对response编码
         */
    //    pipeline.addLast("http-encoder", new HttpResponseEncoder());

        pipeline.addLast("http-server-encoder", new HttpServerCodec());

        pipeline.addLast("http-aggregator", new HttpObjectAggregator(2048));
        /**
         * 压缩
         * Compresses an HttpMessage and an HttpContent in gzip or deflate encoding
         * while respecting the "Accept-Encoding" header.
         * If there is no matching encoding, no compression is done.
         */
        pipeline.addLast("http-deflater", new HttpContentCompressor());

        pipeline.addLast("http-chunked", new ChunkedWriteHandler());

        pipeline.addLast("handler", new HttpServerHandler());
    }
}
