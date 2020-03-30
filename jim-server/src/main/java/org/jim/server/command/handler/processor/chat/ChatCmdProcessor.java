package org.jim.server.command.handler.processor.chat;

import org.jim.common.ImChannelContext;
import org.jim.common.ImPacket;
import org.jim.server.command.handler.processor.SingleProtocolCmdProcessor;
/**
 * 聊天请求cmd业务处理器接口
 * @author WChao
 * @date 2018年4月2日 下午3:21:01
 */
public interface ChatCmdProcessor extends SingleProtocolCmdProcessor {
	/**
	 * 聊天cmd业务处理器处理方法;
	 * @param chatPacket
	 * @param imChannelContext
	 * @throws Exception
	 */
	void handler(ImPacket chatPacket, ImChannelContext imChannelContext);
}
