package org.jim.server.command.handler;

import org.jim.common.ImChannelContext;
import org.jim.common.Jim;
import org.jim.common.ImPacket;
import org.jim.common.ImStatus;
import org.jim.common.exception.ImException;
import org.jim.common.packets.CloseReqBody;
import org.jim.common.packets.Command;
import org.jim.common.packets.RespBody;
import org.jim.common.utils.ImKit;
import org.jim.common.utils.JsonKit;
import org.jim.server.command.AbstractCmdHandler;
import org.jim.server.handler.ProtocolManager;

/**
 * 版本: [1.0]
 * 功能说明: 关闭请求cmd命令处理器
 * @author : WChao 创建时间: 2017年9月21日 下午3:33:23
 */
public class CloseReqHandler extends AbstractCmdHandler
{
	@Override
	public ImPacket handler(ImPacket packet, ImChannelContext imChannelContext) throws ImException
	{
		CloseReqBody closeReqBody = null;
		try{
			closeReqBody = JsonKit.toBean(packet.getBody(),CloseReqBody.class);
		}catch (Exception e) {
			//关闭请求消息格式不正确
			return ProtocolManager.Converter.respPacket(new RespBody(Command.COMMAND_CLOSE_REQ, ImStatus.C10020), imChannelContext);
		}
		Jim.bSend(imChannelContext, ProtocolManager.Converter.respPacket(new RespBody(Command.COMMAND_CLOSE_REQ, ImStatus.C10021), imChannelContext));
		if(closeReqBody == null || closeReqBody.getUserid() == null){
			Jim.remove(imChannelContext, "收到关闭请求");
		}else{
			String userId = closeReqBody.getUserid();
			Jim.remove(userId, "收到关闭请求!");
		}
		return null;
	}

	@Override
	public Command command() {
		return Command.COMMAND_CLOSE_REQ;
	}
}
