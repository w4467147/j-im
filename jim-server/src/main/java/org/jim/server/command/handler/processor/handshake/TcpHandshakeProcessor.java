/**
 * 
 */
package org.jim.server.command.handler.processor.handshake;

import org.jim.common.ImChannelContext;
import org.jim.common.ImPacket;
import org.jim.common.ImSessionContext;
import org.jim.common.exception.ImException;
import org.jim.common.packets.Command;
import org.jim.common.packets.HandshakeBody;
import org.jim.common.packets.RespBody;
import org.jim.common.tcp.TcpSessionContext;
import org.jim.server.handler.ProtocolManager;

/**
 * 版本: [1.0]
 * 功能说明: 
 * 作者: WChao 创建时间: 2017年9月11日 下午8:11:34
 */
public class TcpHandshakeProcessor implements HandshakeCmdProcessor {

	@Override
	public ImPacket handshake(ImPacket packet, ImChannelContext channelContext) throws ImException {
		RespBody handshakeBody = new RespBody(Command.COMMAND_HANDSHAKE_RESP,new HandshakeBody(Protocol.HANDSHAKE_BYTE));
		ImPacket handshakePacket = ProtocolManager.Converter.respPacket(handshakeBody,channelContext);
		return handshakePacket;
	}

	/**
	 * 握手成功后
	 * @param packet
	 * @param channelContext
	 * @throws ImException
	 * @author Wchao
	 */
	@Override
	public void onAfterHandshake(ImPacket packet, ImChannelContext channelContext)throws ImException {
		
	}

	@Override
	public boolean isProtocol(ImChannelContext channelContext){
		ImSessionContext sessionContext = channelContext.getSessionContext();
		if(sessionContext == null){
			return false;
		}else if(sessionContext instanceof TcpSessionContext){
			return true;
		}
		return false;
	}
	
}
