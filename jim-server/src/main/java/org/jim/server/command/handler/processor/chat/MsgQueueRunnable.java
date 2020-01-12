package org.jim.server.command.handler.processor.chat;

import org.jim.common.ImChannelContext;
import org.jim.common.ImConst;
import org.jim.common.ImPacket;
import org.jim.common.packets.ChatBody;
import org.jim.common.packets.Command;
import org.jim.common.utils.ChatKit;
import org.jim.server.command.CommandManager;
import org.jim.server.command.handler.ChatReqHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.utils.queue.FullWaitQueue;
import org.tio.utils.queue.TioFullWaitQueue;
import org.tio.utils.thread.pool.AbstractQueueRunnable;

import java.util.concurrent.Executor;

/**
 * @author WChao
 * @date 2018年4月3日 上午10:47:40
 */
public class MsgQueueRunnable extends AbstractQueueRunnable<ImPacket> {
	
	private Logger log = LoggerFactory.getLogger(MsgQueueRunnable.class);
	
	private ImChannelContext imChannelContext;
	
	private AsyncChatMessageProcessor chatMessageProcessor;
	
	@Override
	public FullWaitQueue<ImPacket> getMsgQueue() {
		return new TioFullWaitQueue(Integer.MAX_VALUE,true);
	}

	public MsgQueueRunnable(ImChannelContext imChannelContext, Executor executor) {
		super(executor);
		this.imChannelContext = imChannelContext;
		ChatReqHandler chatReqHandler = CommandManager.getCommand(Command.COMMAND_CHAT_REQ,ChatReqHandler.class);
		chatMessageProcessor = chatReqHandler.getProcessor(ImConst.BASE_ASYNC_CHAT_MESSAGE_PROCESSOR,BaseAsyncChatMessageProcessor.class).get(0);
	}

	@Override
	public void runTask() {
		ImPacket packet = null;
		while ((packet = this.getMsgQueue().poll()) != null) {
			if(chatMessageProcessor != null){
				try {
					ChatBody chatBody = ChatKit.toChatBody(packet.getBody(), imChannelContext);
					chatMessageProcessor.handler(chatBody, imChannelContext);
				} catch (Exception e) {
					log.error(e.toString(),e);
				}
			}
		}
	}
}
