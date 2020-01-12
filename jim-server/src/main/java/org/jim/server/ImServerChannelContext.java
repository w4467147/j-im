package org.jim.server;

import org.jim.common.ImChannelContext;
import org.jim.common.ImHandler;
import org.jim.common.config.ImConfig;
import org.jim.common.protocol.AbstractProtocol;
import org.jim.common.protocol.IProtocol;
import org.jim.server.handler.AbstractProtocolHandler;
import org.tio.core.ChannelContext;
import org.tio.utils.thread.pool.AbstractQueueRunnable;

/**
 * @ClassName ImServerChannelContext
 * @Description TODO
 * @Author WChao
 * @Date 2020/1/5 23:56
 * @Version 1.0
 **/
public class ImServerChannelContext extends ImChannelContext {

    protected AbstractQueueRunnable msgQue;

    protected AbstractProtocolHandler protocolHandler;

    public ImServerChannelContext(ImConfig imConfig, ChannelContext tioChannelContext) {
        super(imConfig, tioChannelContext);
    }

    public AbstractQueueRunnable getMsgQue() {
        return msgQue;
    }

    public void setMsgQue(AbstractQueueRunnable msgQue) {
        this.msgQue = msgQue;
    }

    public AbstractProtocolHandler getProtocolHandler() {
        return protocolHandler;
    }

    public void setProtocolHandler(AbstractProtocolHandler protocolHandler) {
        this.protocolHandler = protocolHandler;
    }

}
