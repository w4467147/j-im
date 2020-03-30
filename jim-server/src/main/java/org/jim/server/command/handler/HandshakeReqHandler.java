package org.jim.server.command.handler;

import org.apache.commons.collections4.CollectionUtils;
import org.jim.common.ImChannelContext;
import org.jim.common.Jim;
import org.jim.common.ImPacket;
import org.jim.common.exception.ImException;
import org.jim.common.http.HttpRequest;
import org.jim.common.packets.Command;
import org.jim.common.ws.WsSessionContext;
import org.jim.server.command.AbstractCmdHandler;
import org.jim.server.command.handler.processor.handshake.HandshakeCmdProcessor;
import java.util.List;
import java.util.Objects;

/**
 * 版本: [1.0]
 * 功能说明: 心跳cmd命令处理器
 * @author : WChao 创建时间: 2017年9月21日 下午3:33:23
 */
public class HandshakeReqHandler extends AbstractCmdHandler {
	
	@Override
	public ImPacket handler(ImPacket packet, ImChannelContext channelContext) throws ImException {

		HandshakeCmdProcessor handshakeProcessor = this.getMultiProcessor(channelContext,HandshakeCmdProcessor.class);
		if(Objects.isNull(handshakeProcessor)){
			Jim.remove(channelContext, "没有对应的握手协议处理器HandshakeCmdProcessor...");
			return null;
		}
		ImPacket handShakePacket = handshakeProcessor.handshake(packet, channelContext);
		if (handShakePacket == null) {
			Jim.remove(channelContext, "业务层不同意握手");
			return null;
		}
		Jim.send(channelContext, handShakePacket);
		WsSessionContext wsSessionContext = (WsSessionContext) channelContext.getSessionContext();
		HttpRequest request = wsSessionContext.getHandshakeRequestPacket();
		handshakeProcessor.onAfterHandshake(request, channelContext);
		return null;
	}

	@Override
	public Command command() {
		return Command.COMMAND_HANDSHAKE_REQ;
	}
}
