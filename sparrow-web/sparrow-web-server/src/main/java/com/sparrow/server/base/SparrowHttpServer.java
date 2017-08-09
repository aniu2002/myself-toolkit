package com.sparrow.server.base;

import com.sparrow.http.ThreadPoolHttpServer;
import com.sparrow.http.check.SessionCheck;
import com.sparrow.http.command.CommandController;
import com.sparrow.http.handler.CommandHandler;
import com.sparrow.http.handler.FileUploadHandler;
import com.sparrow.server.handler.SecurityCommandHandler;
import com.sparrow.server.handler.SecuritySessionCheck;

/**
 * Created by Administrator on 2016/3/10 0010.
 */
public class SparrowHttpServer extends ThreadPoolHttpServer {
    public SparrowHttpServer() {
        this(new SecuritySessionCheck());
    }

    public SparrowHttpServer(SessionCheck sessionCheck) {
        super(sessionCheck);
    }

    @Override
    protected CommandHandler createCommandHandler(CommandController controller) {
        return new SecurityCommandHandler(controller);
    }

    @Override
    protected CommandHandler createCommandHandler(CommandController controller, FileUploadHandler fileUploadHandler) {
        return new SecurityCommandHandler(controller, fileUploadHandler);
    }
}
