package org.jim.server.command.handler.processor.chat;

import org.jim.common.ImChannelContext;
import org.jim.common.packets.ChatBody;
import org.jim.common.utils.JsonKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author WChao
 * @date 2018年4月3日 下午1:12:30
 */
public class DefaultAsyncChatMessageProcessor extends BaseAsyncChatMessageProcessor {

	private static Logger logger = LoggerFactory.getLogger(DefaultAsyncChatMessageProcessor.class);

	@Override
	public void doHandler(ChatBody chatBody, ImChannelContext imChannelContext){
		logger.info("默认交由业务处理聊天记录示例,用户自己继承BaseAsyncChatMessageProcessor即可:{}", JsonKit.toJSONString(chatBody));
	}
}
