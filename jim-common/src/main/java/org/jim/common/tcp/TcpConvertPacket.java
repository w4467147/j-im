/**
 * 
 */
package org.jim.common.tcp;

import org.jim.common.ImChannelContext;
import org.jim.common.ImPacket;
import org.jim.common.ImSessionContext;
import org.jim.common.packets.Command;
import org.jim.common.protocol.IProtocolConverter;

/**
 * TCP协议消息转化包
 * @author WChao
 *
 */
public class TcpConvertPacket implements IProtocolConverter {

	/**
	 * 转TCP协议响应包;
	 */
	@Override
	public ImPacket RespPacket(byte[] body, Command command, ImChannelContext imChannelContext) {
		ImSessionContext sessionContext = imChannelContext.getSessionContext();
		if(sessionContext instanceof TcpSessionContext){
			TcpPacket tcpPacket = new TcpPacket(command,body);
			TcpServerEncoder.encode(tcpPacket, imChannelContext.getImConfig(), imChannelContext);
			tcpPacket.setCommand(command);
			return tcpPacket;
		}
		return null;
	}
	/**
	 * 转TCP协议请求包;
	 */
	@Override
	public ImPacket ReqPacket(byte[] body, Command command, ImChannelContext channelContext) {
		Object sessionContext = channelContext.getSessionContext();
		if(sessionContext instanceof TcpSessionContext){
			TcpPacket tcpPacket = new TcpPacket(command,body);
			TcpServerEncoder.encode(tcpPacket, channelContext.getImConfig(), channelContext);
			tcpPacket.setCommand(command);
			return tcpPacket;
		}
		return null;
	}

}
