/**
 * 
 */
package org.jim.common.ws;

import java.nio.ByteBuffer;

import org.jim.common.ImChannelContext;
import org.jim.common.ImPacket;
import org.jim.common.ImSessionContext;
import org.jim.common.exception.ImException;
import org.jim.common.http.HttpRequest;
import org.jim.common.http.HttpRequestDecoder;
import org.jim.common.protocol.AbstractProtocol;
import org.jim.common.protocol.IProtocolConverter;
import org.jim.common.utils.ImUtils;
/**
 * WebSocket协议判断器
 * @author WChao
 *
 */
public class WsProtocol extends AbstractProtocol {

	@Override
	public String name() {
		return Protocol.WEB_SOCKET;
	}

	public WsProtocol(IProtocolConverter converter){
		super(converter);
	}
	
	@Override
	protected void init(ImChannelContext imChannelContext) {
		imChannelContext.setSessionContext(new WsSessionContext(imChannelContext));
		ImUtils.setClient(imChannelContext);
	}

	@Override
	protected boolean validateProtocol(ImSessionContext imSessionContext) throws ImException {
		if(imSessionContext instanceof WsSessionContext) {
			return true;
		}
		return false;
	}

	@Override
	protected boolean validateProtocol(ByteBuffer buffer, ImChannelContext imChannelContext) throws ImException {
		//第一次连接;
		HttpRequest request = HttpRequestDecoder.decode(buffer, imChannelContext,false);
		if(request.getHeaders().get(Http.RequestHeaderKey.Sec_WebSocket_Key) != null)
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean validateProtocol(ImPacket imPacket) throws ImException {
		if(imPacket instanceof WsPacket){
			return true;
		}
		return false;
	}

}
