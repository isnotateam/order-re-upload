package com.choice.orderupload.baton;

import com.choice.orderupload.config.ApplicationParameter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * 实现每个接收器与监听器之间保持长连接，能够进行快速通信
 * Created by lizhiqiang on 2017/2/10.
 */
@Component
public class Client {
    /**
     * log4j日志工具
     */
    private static final Logger PLOG = LoggerFactory.getLogger(Client.class);

    @Autowired
    private ApplicationParameter applicationParameter;
    /**
     * 下一步服务的通道，具体信息由它进行发送
     */
    public static Channel channel = null;

    public void createBootstrap(Bootstrap bootstrap, EventLoopGroup group) {
        try {
            try {
                if (bootstrap != null) {
                    final MyInboundHandler handler = new MyInboundHandler(this);
                    bootstrap.group(group)
                            .channel(NioSocketChannel.class)
                            .remoteAddress(new InetSocketAddress(applicationParameter.getNextHost(), applicationParameter.getNextPort()))
                            .option(ChannelOption.SO_KEEPALIVE, true)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    socketChannel.pipeline().addLast(handler);
                                    socketChannel.pipeline().addLast(new NotifierReaderHandler());
                                }
                            });
                    bootstrap.connect().addListener(new ConnectionListener(this));
                    System.out.println("created..");
                    ChannelFuture cf = bootstrap.connect().sync(); // 异步连接服务器
                    System.out.println("connected..."); // 连接完成
                    cf.channel().closeFuture().sync(); // 异步等待关闭连接channel
                    System.out.println("closed.."); // 关闭完成
                }
            } finally {
                group.shutdownGracefully().sync();
            }
        } catch (InterruptedException e) {
            System.out.println("Client启动失败！");
        }
    }
}