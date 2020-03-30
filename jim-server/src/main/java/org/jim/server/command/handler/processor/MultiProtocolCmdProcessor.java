package org.jim.server.command.handler.processor;

import org.jim.common.ImChannelContext;
import org.jim.common.ImConst;

/**
 * @date 2020-03-19
 * @author WChao
 */
public interface MultiProtocolCmdProcessor extends ImConst {
    /**
     * 不同协议判断方法
     * @param imChannelContext
     * @return
     */
    boolean isProtocol(ImChannelContext imChannelContext);
}
