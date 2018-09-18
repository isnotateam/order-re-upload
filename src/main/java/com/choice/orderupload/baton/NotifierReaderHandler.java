package com.choice.orderupload.baton;

import com.choice.orderupload.utils.SpringContextUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 与下一步服务之间的连接状况处理类
 * Created by linjincheng on 2018/9/17.
 */
public class NotifierReaderHandler extends ChannelInboundHandlerAdapter {
    /**
     * log4j日志工具
     */
    private static final Logger PLOG = LoggerFactory.getLogger(NotifierReaderHandler.class);

    public NotifierReaderHandler() {
    }

    /**
     * 连接建立事件处理
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Client.channel = ctx.channel();
    }

    /**
     * 消息读取完毕事件处理
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    /**
     * 连接异常事件读取
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Client.channel = null;
    }

    /**
     * 连接断开事件处理
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Client.channel = null;
        super.channelInactive(ctx);
    }
}