package com.choice.orderupload.config;

import com.choice.orderupload.baton.BatonController;
import com.choice.orderupload.baton.BatonEvent;
import com.choice.orderupload.baton.BatonEventFactory;
import com.choice.orderupload.baton.BatonEventHandler;
import com.choice.orderupload.utils.SpringContextUtil;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.dsl.Disruptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author 林金成
 * @date 2018/9/14 15:45
 */
@Configuration
public class ApplicationConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(BatonController.class)
    public BatonController batonController() {
        Executor executor = Executors.newCachedThreadPool();
        BatonEventFactory factory = new BatonEventFactory();
        int bufferSize = 1024;
        Disruptor<BatonEvent> disruptor = new Disruptor<BatonEvent>(factory, bufferSize, executor);
        disruptor.handleEventsWith(new BatonEventHandler());
        disruptor.setDefaultExceptionHandler(new ExceptionHandler() {
            /**
             * 解决下一步服务中断的特殊情况
             * @param ex
             * @param sequence
             * @param event
             */
            @Override
            public void handleEventException(Throwable ex, long sequence, Object event) {
                if (event instanceof BatonEvent) {
                    SpringContextUtil.getBean(BatonController.class).sendFile(((BatonEvent) event).getGroupid(), ((BatonEvent) event).getStoreid(), ((BatonEvent) event).getWorkdate(), ((BatonEvent) event).getFilepath());
                }
            }

            @Override
            public void handleOnStartException(Throwable ex) {
                ex.printStackTrace();
            }

            @Override
            public void handleOnShutdownException(Throwable ex) {
                ex.printStackTrace();
            }
        });
        disruptor.start();
        BatonController batonController = new BatonController(disruptor.getRingBuffer());
        return batonController;
    }
}