package com.choice.orderupload.baton;

import com.lmax.disruptor.EventFactory;

/**
 * 通知事件的构造工厂
 * Created by lizhiqiang on 2017-07-03.
 */
public class BatonEventFactory implements EventFactory<BatonEvent> {
    @Override
    public BatonEvent newInstance() {
        return new BatonEvent();
    }
}
