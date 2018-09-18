package com.choice.orderupload.config;

import com.choice.orderupload.baton.Client;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.boot.CommandLineRunner;

/**
 * @author 林金成
 * @date 2018/9/14 15:20
 */
//@Component
//@Order(1)
public class NettyClientRunner implements CommandLineRunner {
    //    @Autowired
    private Client client;

    @Override
    public void run(String... args) {
        client.createBootstrap(new Bootstrap(), new NioEventLoopGroup());
    }
}