package com.choice.orderupload.baton;

import com.lmax.disruptor.RingBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务消息发送控制类
 */
public class BatonController {
    /**
     * log4j日志工具
     */
    private static final Logger PLOG = LoggerFactory.getLogger(BatonController.class);

    /**
     * 消息发送队列
     */
    private RingBuffer<BatonEvent> ringBuffer;

    /**
     * 构造函数
     */
    public BatonController(RingBuffer<BatonEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    /**
     * 发送文件
     */
    public void sendFile(String tenantId, String vscode, String workdate, String filepath) {
        PLOG.info("添加新的发送任务{},{},{},{}到发送器缓存", tenantId, vscode, workdate, filepath);
        //将发送任务缓存到一个队列中，然后由另一个线程负责发送
        long sequence = ringBuffer.next();  // Grab the next sequence
        try {
            BatonEvent event = ringBuffer.get(sequence); // Get the entry in the Disruptor
            event.setGroupid(tenantId);
            event.setStoreid(vscode);
            event.setWorkdate(workdate);
            event.setFilepath(filepath);
            PLOG.info("添加新的发送任务成功");
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}