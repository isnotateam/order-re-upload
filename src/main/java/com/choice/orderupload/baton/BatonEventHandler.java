package com.choice.orderupload.baton;

import com.choice.orderupload.utils.SpringContextUtil;
import com.lmax.disruptor.EventHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 发送订单文件到云平台
 */
public class BatonEventHandler implements EventHandler<BatonEvent> {
    /**
     * log4j日志工具
     */
    private static final Logger PLOG = LoggerFactory.getLogger(BatonEventHandler.class);

    @Override
    public void onEvent(BatonEvent batonEvent, long l, boolean b) {
        this.sendFile(batonEvent.getGroupid(), batonEvent.getStoreid(), batonEvent.getFilepath());
    }

    /**
     * 发送文件，比如组装服务将整理好的标准数据持久化到硬盘，发送这个文件到下一步
     */
    private void sendFile(String tenantId, String vscode, String filepath) {
        boolean connected = connect();
        if (!connected) {
            PLOG.error("next baton server is down!!!try other next baton server...");
            sendFile(tenantId, vscode, filepath);
            return;
        }
        try {
            byte[] req = Files.readAllBytes(Paths.get(filepath));
            ByteBuf message = Unpooled.buffer(req.length + 2);
            message.writeBytes(req);
            message.writeBytes("\n\r".getBytes(CharsetUtil.UTF_8));
            PLOG.debug("send task to deal: tenantId:" + tenantId + ", vscode:" + vscode + ", message:" + message);
            Client.channel.writeAndFlush(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是或否连接，如若不连接则创建与下一步服务的连接
     *
     * @return
     */
    private synchronized boolean connect() {
        if (Client.channel == null) {
            Client client = SpringContextUtil.getBean(Client.class);
            client.createBootstrap(new Bootstrap(), new NioEventLoopGroup());
        }

        if (Client.channel == null) {
            return false;
        } else {
            return true;
        }
    }
}