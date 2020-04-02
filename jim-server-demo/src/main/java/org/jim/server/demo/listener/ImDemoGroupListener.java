package org.jim.server.demo.listener;

import org.jim.common.ImChannelContext;
import org.jim.common.Jim;
import org.jim.common.ImPacket;
import org.jim.common.ImSessionContext;
import org.jim.common.exception.ImException;
import org.jim.common.listener.ImGroupListener;
import org.tio.core.ChannelContext;
import org.jim.common.packets.Client;
import org.jim.common.packets.Command;
import org.jim.common.packets.ExitGroupNotifyRespBody;
import org.jim.common.packets.RespBody;
import org.jim.common.packets.User;

/**
 * @author WChao 
 * 2017年5月13日 下午10:38:36
 */
public class ImDemoGroupListener implements ImGroupListener {
	@Override
	public void onAfterBind(ImChannelContext imChannelContext, String group) throws ImException {
		System.out.println("===绑定成功:"+group);
	}

	/**
	 * @param channelContext
	 * @param group
	 * @throws Exception
	 * @author: WChao
	 */
	@Override
	public void onAfterUnbind(ImChannelContext channelContext, String group) throws ImException {
		//发退出房间通知  COMMAND_EXIT_GROUP_NOTIFY_RESP
		ExitGroupNotifyRespBody exitGroupNotifyRespBody = new ExitGroupNotifyRespBody();
		exitGroupNotifyRespBody.setGroup(group);
		Client client = channelContext.getSessionContext().getClient();
		if(client == null){
			return;
		}
		User clientUser = client.getUser();
		if(clientUser == null) {
			return;
		}
		User notifyUser = new User(clientUser.getUserId(),clientUser.getNick());
		exitGroupNotifyRespBody.setUser(notifyUser);
		
		RespBody respBody = new RespBody(Command.COMMAND_EXIT_GROUP_NOTIFY_RESP,exitGroupNotifyRespBody);
		ImPacket imPacket = new ImPacket(Command.COMMAND_EXIT_GROUP_NOTIFY_RESP, respBody.toByte());
		Jim.sendToGroup(group, imPacket);
		
	}
}
