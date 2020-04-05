package org.jim.server.command.handler;

import org.apache.commons.lang3.StringUtils;
import org.jim.common.*;
import org.jim.common.exception.ImException;
import org.jim.server.command.handler.processor.group.GroupCmdProcessor;
import org.jim.server.handler.ProtocolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jim.common.packets.Command;
import org.jim.common.packets.Group;
import org.jim.common.packets.JoinGroupNotifyRespBody;
import org.jim.common.packets.JoinGroupRespBody;
import org.jim.common.packets.JoinGroupResult;
import org.jim.common.packets.RespBody;
import org.jim.common.packets.User;
import org.jim.common.utils.JsonKit;
import org.jim.server.command.AbstractCmdHandler;
import java.util.Objects;

/**
 * 
 * 版本: [1.0]
 * 功能说明: 加入群组消息cmd命令处理器
 * @author : WChao 创建时间: 2017年9月21日 下午3:33:23
 */
public class JoinGroupReqHandler extends AbstractCmdHandler {
	
	private static Logger log = LoggerFactory.getLogger(JoinGroupReqHandler.class);
	
	@Override
	public ImPacket handler(ImPacket packet, ImChannelContext imChannelContext) throws ImException {
		//绑定群组;
		ImPacket joinGroupRespPacket = bindGroup(packet, imChannelContext);
		//发送进房间通知;
		joinGroupNotify(packet,imChannelContext);
		return joinGroupRespPacket;
	}
	/**
	 * 发送进房间通知;
	 * @param packet
	 * @param imChannelContext
	 */
	public void joinGroupNotify(ImPacket packet, ImChannelContext imChannelContext)throws ImException{
		ImSessionContext imSessionContext = imChannelContext.getSessionContext();
		
		User clientUser = imSessionContext.getClient().getUser();
		User notifyUser = new User(clientUser.getUserId(),clientUser.getNick());
		
		Group joinGroup = JsonKit.toBean(packet.getBody(),Group.class);
		String groupId = joinGroup.getGroupId();
		//发进房间通知  COMMAND_JOIN_GROUP_NOTIFY_RESP
		JoinGroupNotifyRespBody joinGroupNotifyRespBody = new JoinGroupNotifyRespBody(Command.COMMAND_JOIN_GROUP_NOTIFY_RESP, ImStatus.C10011)
				.setGroup(groupId)
				.setUser(notifyUser);
		Jim.sendToGroup(groupId, ProtocolManager.Converter.respPacket(joinGroupNotifyRespBody,imChannelContext));
	}
	/**
	 * 绑定群组
	 * @param packet
	 * @param imChannelContext
	 * @return
	 * @throws ImException
	 */
	public ImPacket bindGroup(ImPacket packet, ImChannelContext imChannelContext) throws ImException {
		if (packet.getBody() == null) {
			throw new ImException("body is null");
		}
		Group joinGroup = JsonKit.toBean(packet.getBody(),Group.class);
		String groupId = joinGroup.getGroupId();
		if (StringUtils.isBlank(groupId)) {
			log.error("group is null,{}", imChannelContext);
			Jim.close(imChannelContext, "group is null when join group");
			return null;
		}
		//实际绑定之前执行处理器动作
		GroupCmdProcessor groupCmdProcessor = (GroupCmdProcessor)this.getSingleProcessor();

		JoinGroupRespBody joinGroupRespBody = new JoinGroupRespBody(Command.COMMAND_JOIN_GROUP_RESP,ImStatus.C10011);
		//当有群组处理器时候才会去处理
		if(Objects.nonNull(groupCmdProcessor)){
			joinGroupRespBody = groupCmdProcessor.join(joinGroup, imChannelContext);
			if (joinGroupRespBody == null || JoinGroupResult.JOIN_GROUP_RESULT_OK.getNumber() != joinGroupRespBody.getResult().getNumber()) {
				RespBody joinRespBody = new RespBody(Command.COMMAND_JOIN_GROUP_RESP, ImStatus.C10012).setData(joinGroupRespBody);
				ImPacket respPacket = ProtocolManager.Converter.respPacket(joinRespBody, imChannelContext);
				return respPacket;
			}
		}
		//处理完处理器内容后
		Jim.bindGroup(imChannelContext, groupId);

		//回一条消息，告诉对方进群结果
		joinGroupRespBody.setGroup(groupId);
		//先定义为操作成功
		joinGroupRespBody.setResult(JoinGroupResult.JOIN_GROUP_RESULT_OK);
		joinGroupRespBody.setData(joinGroupRespBody);
		ImPacket respPacket = ProtocolManager.Converter.respPacket(joinGroupRespBody, imChannelContext);
		return respPacket;
	}
	@Override
	public Command command() {
		
		return Command.COMMAND_JOIN_GROUP_REQ;
	}
}
