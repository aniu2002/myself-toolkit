package com.sparrow.server;

import com.sparrow.core.config.SystemConfig;
import com.sparrow.http.ThreadPoolHttpServer;
import com.sparrow.http.command.CommandController;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.OkResponse;
import com.sparrow.http.command.resp.TextResponse;
import com.sparrow.http.filter.HttpFilterFactory;
import com.sparrow.http.handler.CommandHandler;
import com.sparrow.http.handler.FileUploadHandler;
import com.sparrow.server.handler.RestActionHandler;

public class Starter {
    static final Response ok = new OkResponse();
    static final Response t = new TextResponse("我在测试");

    public static void test(String name) {
        System.out.println(name.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")
                .replaceAll("([a-z\\d])([A-Z])", "$1_$2").toLowerCase());
    }

    public static void main(String args[]) {
        ThreadPoolHttpServer server = new ThreadPoolHttpServer() {
            @Override
            protected CommandHandler createCommandHandler(CommandController controller) {
                return new CommandHandler(controller);
            }

            @Override
            protected CommandHandler createCommandHandler(CommandController controller, FileUploadHandler fileUploadHandler) {
                return new CommandHandler(controller, fileUploadHandler);
            }
        };
        server.setIp("127.0.0.1");
        server.setPort(9081);
        server.init();

        String rootPath = SystemConfig.WEB_ROOT;
        server.addFileHandler("/app", rootPath);
        server.addFileHandler("/img", "F:/99bt/images");
        //server.addHttpHandler("/event", new LoopMessageHandler());
        server.addUploadHandler("/upload", "E:/xx");
        // server.addHttpHandler("/rest", new AnnotationActionHandler());
        server.addHttpHandler("/rest",
                new RestActionHandler(HttpFilterFactory.getHttpdFilter()));
        server.start();
    }
}
