/**
 * 
 */
package org.jim.server.tcp;

import org.apache.log4j.Logger;
import org.jim.common.ImChannelContext;
import org.jim.common.Jim;
import org.jim.common.ImPacket;
import org.jim.common.ImStatus;
import org.jim.common.config.ImConfig;
import org.jim.common.exception.ImDecodeException;
import org.jim.common.exception.ImException;
import org.jim.common.packets.Command;
import org.jim.common.packets.RespBody;
import org.jim.common.protocol.AbstractProtocol;
import org.jim.common.tcp.*;
import org.jim.server.command.AbstractCmdHandler;
import org.jim.server.command.CommandManager;
import org.jim.server.config.ImServerConfig;
import org.jim.server.handler.AbstractProtocolHandler;
import java.nio.ByteBuffer;

/**
 * 版本: [1.0]
 * 功能说明: 
 * @author : WChao 创建时间: 2017年8月3日 下午7:44:48
 */
public class TcpProtocolHandler extends AbstractProtocolHandler {
	
	Logger logger = Logger.getLogger(TcpProtocolHandler.class);

	public TcpProtocolHandler(){
		this.protocol = new TcpProtocol(new TcpConvertPacket());
	}

	public TcpProtocolHandler(AbstractProtocol protocol){
		super(protocol);
	}

	@Override
	public void init(ImServerConfig imServerConfig) {
		logger.info("J-IM TCP协议初始化完毕...");
	}
	@Override
	public ByteBuffer encode(ImPacket imPacket, ImConfig imConfig, ImChannelContext imChannelContext) {
		TcpPacket tcpPacket = (TcpPacket)imPacket;
		return TcpServerEncoder.encode(tcpPacket, imConfig, imChannelContext);
	}

	@Override
	public void handler(ImPacket packet, ImChannelContext imChannelContext)throws ImException {
		TcpPacket tcpPacket = (TcpPacket)packet;
		AbstractCmdHandler cmdHandler = CommandManager.getCommand(tcpPacket.getCommand());
		if(cmdHandler == null){
			ImPacket imPacket = new ImPacket(Command.COMMAND_UNKNOW, new RespBody(Command.COMMAND_UNKNOW,ImStatus.C10017).toByte());
			Jim.send(imChannelContext, imPacket);
			return;
		}
		ImPacket response = cmdHandler.handler(tcpPacket, imChannelContext);
		if(response != null && tcpPacket.getSynSeq() < 1){
			Jim.send(imChannelContext, response);
		}
	}

	@Override
	public TcpPacket decode(ByteBuffer buffer, int limit, int position, int readableLength, ImChannelContext imChannelContext)throws ImDecodeException {
		TcpPacket tcpPacket = TcpServerDecoder.decode(buffer, imChannelContext);
		return tcpPacket;
	}

}
