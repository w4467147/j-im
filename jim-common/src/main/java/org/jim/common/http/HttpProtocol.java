/**
 * 
 */
package org.jim.common.http;

import java.nio.ByteBuffer;
import org.jim.common.ImChannelContext;
import org.jim.common.ImPacket;
import org.jim.common.ImSessionContext;
import org.jim.common.exception.ImException;
import org.jim.common.http.session.HttpSession;
import org.jim.common.protocol.AbstractProtocol;
import org.jim.common.protocol.IProtocolConverter;
import org.jim.common.utils.ImUtils;
/**
 *
 * Http协议校验器
 * @author WChao
 *
 */
public class HttpProtocol extends AbstractProtocol {

	@Override
	public String name() {
		return Protocol.HTTP;
	}

	public HttpProtocol(IProtocolConverter protocolConverter){
		super(protocolConverter);
	}

	@Override
	protected void init(ImChannelContext imChannelContext) {
		imChannelContext.setSessionContext(new HttpSession(imChannelContext));
		ImUtils.setClient(imChannelContext);
	}

	@Override
	public boolean validateProtocol(ImSessionContext imSessionContext) throws ImException {
		if(imSessionContext instanceof HttpSession) {
			return true;
		}
		return false;
	}

	@Override
	public boolean validateProtocol(ByteBuffer buffer, ImChannelContext imChannelContext) throws ImException {
		HttpRequest request = HttpRequestDecoder.decode(buffer, imChannelContext,false);
		if(request.getHeaders().get(Http.RequestHeaderKey.Sec_WebSocket_Key) == null)
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean validateProtocol(ImPacket imPacket) throws ImException {
		if(imPacket instanceof HttpPacket){
			return true;
		}
		return false;
	}

}
