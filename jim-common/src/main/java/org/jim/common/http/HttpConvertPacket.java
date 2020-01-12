/**
 * 
 */
package org.jim.common.http;

import org.jim.common.ImChannelContext;
import org.jim.common.ImConst;
import org.jim.common.ImPacket;
import org.jim.common.ImSessionContext;
import org.jim.common.http.session.HttpSession;
import org.jim.common.packets.Command;
import org.jim.common.protocol.IProtocolConverter;
/**
 * HTTP协议消息转化包
 * @author WChao
 *
 */
public class HttpConvertPacket implements IProtocolConverter {

	/**
	 * 转HTTP协议响应包;
	 */
	@Override
	public ImPacket RespPacket(byte[] body, Command command, ImChannelContext channelContext) {
		ImSessionContext sessionContext = channelContext.getSessionContext();
		if(sessionContext instanceof HttpSession){
			HttpRequest request = (HttpRequest)channelContext.getAttribute(ImConst.HTTP_REQUEST);
			HttpResponse response = new HttpResponse(request,request.getHttpConfig());
			response.setBody(body, request);
			response.setCommand(command);
			return response;
		}
		return null;
	}

	@Override
	public ImPacket ReqPacket(byte[] body, Command command, ImChannelContext channelContext) {
		
		return null;
	}

}
