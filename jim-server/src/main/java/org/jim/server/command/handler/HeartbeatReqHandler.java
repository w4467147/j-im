package org.jim.server.command.handler;

import org.jim.common.ImChannelContext;
import org.jim.common.ImPacket;
import org.jim.common.exception.ImException;
import org.jim.common.packets.Command;
import org.jim.common.packets.HeartbeatBody;
import org.jim.common.packets.RespBody;
import org.jim.server.command.AbstractCmdHandler;
import org.jim.server.handler.ProtocolManager;

/**
 *
 */
public class HeartbeatReqHandler extends AbstractCmdHandler
{
	@Override
	public ImPacket handler(ImPacket packet, ImChannelContext channelContext) throws ImException
	{
		RespBody heartbeatBody = new RespBody(Command.COMMAND_HEARTBEAT_REQ).setData(new HeartbeatBody(Protocol.HEARTBEAT_BYTE));
		ImPacket heartbeatPacket = ProtocolManager.Converter.respPacket(heartbeatBody,channelContext);
		return heartbeatPacket;
	}

	@Override
	public Command command() {
		return Command.COMMAND_HEARTBEAT_REQ;
	}
}
