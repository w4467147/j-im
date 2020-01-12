/**
 * 
 */
package org.jim.common.protocol;

import org.jim.common.ImChannelContext;
import org.jim.common.ImPacket;
import org.jim.common.packets.Command;
/**
 * 转换不同协议消息包;
 * @author WChao
 *
 */
public interface IProtocolConverter {
	/**
	 * 转化请求包
	 * @param body
	 * @param command
	 * @param imChannelContext
	 * @return
	 */
	ImPacket ReqPacket(byte[] body,Command command, ImChannelContext imChannelContext);
	/**
	 * 转化响应包
	 * @param body
	 * @param command
	 * @param imChannelContext
	 * @return
	 */
	ImPacket RespPacket(byte[] body,Command command, ImChannelContext imChannelContext);
}
