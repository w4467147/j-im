package org.jim.server.demo.listener;

import org.jim.common.*;
import org.jim.common.exception.ImException;
import org.jim.common.listener.ImGroupListener;
import org.jim.common.packets.*;
import org.jim.common.utils.JsonKit;
import org.jim.server.handler.ProtocolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;

/**
 * @author WChao 
 * 2017年5月13日 下午10:38:36
 */
public class ImDemoGroupListener implements ImGroupListener {

	private  static Logger logger = LoggerFactory.getLogger(ImGroupListener.class);

	@Override
	public void onAfterBind(ImChannelContext imChannelContext, Group group) throws ImException {
		logger.info("群组:{},绑定成功!", JsonKit.toJSONString(group));
		JoinGroupRespBody joinGroupRespBody = JoinGroupRespBody.success();
		//回一条消息，告诉对方进群结果
		joinGroupRespBody.setGroup(group.getGroupId());
		ImPacket respPacket = ProtocolManager.Converter.respPacket(joinGroupRespBody, imChannelContext);
		//Jim.send(imChannelContext, respPacket);
		//发送进房间通知;
		joinGroupNotify(group, imChannelContext);
	}

	@Override
	public void onAfterBind(ImChannelContext imChannelContext, String groupId) throws ImException {

	}

	/**
	 * @param channelContext
	 * @param group
	 * @throws Exception
	 * @author: WChao
	 */
	@Override
	public void onAfterUnbind(ImChannelContext channelContext, Group group) throws ImException {

	}

	@Override
	public void onAfterUnbind(ImChannelContext imChannelContext, String groupId) throws ImException {
		//发退出房间通知  COMMAND_EXIT_GROUP_NOTIFY_RESP
		ExitGroupNotifyRespBody exitGroupNotifyRespBody = new ExitGroupNotifyRespBody();
		exitGroupNotifyRespBody.setGroup(groupId);
		User clientUser = imChannelContext.getSessionContext().getClient().getUser();
		if(clientUser == null) {
			return;
		}
		User notifyUser = new User(clientUser.getUserId(),clientUser.getNick());
		exitGroupNotifyRespBody.setUser(notifyUser);

		RespBody respBody = new RespBody(Command.COMMAND_EXIT_GROUP_NOTIFY_RESP,exitGroupNotifyRespBody);
		ImPacket imPacket = new ImPacket(Command.COMMAND_EXIT_GROUP_NOTIFY_RESP, respBody.toByte());
		Jim.sendToGroup(groupId, ProtocolManager.Converter.respPacket(imPacket, imChannelContext));
	}

	/**
	 * 发送进房间通知;
	 * @param group 群组对象
	 * @param imChannelContext
	 */
	public void joinGroupNotify(Group group, ImChannelContext imChannelContext)throws ImException{
		ImSessionContext imSessionContext = imChannelContext.getSessionContext();
		User clientUser = imSessionContext.getClient().getUser();
		User notifyUser = new User(clientUser.getUserId(),clientUser.getNick());
		String groupId = group.getGroupId();
		//发进房间通知  COMMAND_JOIN_GROUP_NOTIFY_RESP
		JoinGroupNotifyRespBody joinGroupNotifyRespBody = JoinGroupNotifyRespBody.success();
		joinGroupNotifyRespBody.setGroup(groupId).setUser(notifyUser);
		Jim.sendToGroup(groupId, ProtocolManager.Converter.respPacket(joinGroupNotifyRespBody,imChannelContext));
	}

}
