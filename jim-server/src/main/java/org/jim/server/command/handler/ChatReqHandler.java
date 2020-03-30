package org.jim.server.command.handler;

import org.jim.common.ImChannelContext;
import org.jim.common.Jim;
import org.jim.common.ImPacket;
import org.jim.common.exception.ImException;
import org.jim.common.packets.ChatBody;
import org.jim.common.packets.ChatType;
import org.jim.common.packets.Command;
import org.jim.common.packets.RespBody;
import org.jim.common.utils.ChatKit;
import org.jim.server.ImServerChannelContext;
import org.jim.server.command.AbstractCmdHandler;
import org.jim.server.handler.ProtocolManager;
import org.tio.utils.thread.pool.AbstractQueueRunnable;

/**
 * 版本: [1.0]
 * 功能说明: 聊天请求cmd消息命令处理器
 * @author : WChao 创建时间: 2017年9月22日 下午2:58:59
 */
public class ChatReqHandler extends AbstractCmdHandler {

	@Override
	public ImPacket handler(ImPacket packet, ImChannelContext channelContext) throws ImException {
		ImServerChannelContext imServerChannelContext = (ImServerChannelContext)channelContext;
		if (packet.getBody() == null) {
			throw new ImException("body is null");
		}
		ChatBody chatBody = ChatKit.toChatBody(packet.getBody(), channelContext);
		packet.setBody(chatBody.toByte());
		//聊天数据格式不正确
		if(chatBody == null || chatBody.getChatType() == null){
			ImPacket respChatPacket = ProtocolManager.Packet.dataInCorrect(channelContext);
			return respChatPacket;
		}
		/*List<ChatCmdProcessor> chatProcessors = this.getProcessorNotEqualName(new HashSet<>(ImConst.BASE_ASYNC_CHAT_MESSAGE_PROCESSOR),ChatCmdProcessor.class);
		if(CollectionUtils.isNotEmpty(chatProcessors)){
			chatProcessors.forEach(chatProcessor -> chatProcessor.handler(packet,channelContext));
		}*/
		//异步调用业务处理消息接口
		if(ChatType.forNumber(chatBody.getChatType()) != null){
			AbstractQueueRunnable msgQueueRunnable = imServerChannelContext.getMsgQue();
			msgQueueRunnable.addMsg(packet);
			msgQueueRunnable.executor.execute(msgQueueRunnable);
		}
		ImPacket chatPacket = new ImPacket(Command.COMMAND_CHAT_REQ,new RespBody(Command.COMMAND_CHAT_REQ,chatBody).toByte());
		//设置同步序列号;
		chatPacket.setSynSeq(packet.getSynSeq());
		//私聊
		if(ChatType.CHAT_TYPE_PRIVATE.getNumber() == chatBody.getChatType()){
			String toId = chatBody.getTo();
			if(ChatKit.isOnline(toId, getImConfig())){
				Jim.sendToUser(toId, chatPacket);
				//发送成功响应包
				return ProtocolManager.Packet.success(channelContext);
			}else{
				//用户不在线响应包
				return ProtocolManager.Packet.offline(channelContext);
			}
			//群聊
		}else if(ChatType.CHAT_TYPE_PUBLIC.getNumber() == chatBody.getChatType()){
			String group_id = chatBody.getGroup_id();
			Jim.sendToGroup(group_id, chatPacket);
			//发送成功响应包
			return ProtocolManager.Packet.success(channelContext);
		}
		return null;
	}
	@Override
	public Command command() {
		return Command.COMMAND_CHAT_REQ;
	}
}
