/**
 * 
 */
package org.jim.server.command.handler.processor;

import org.jim.common.ImChannelContext;
import org.jim.common.ImConst;
/**
 * 不同协议CMD命令处理接口
 * @author WChao
 *
 */
public interface CmdProcessor extends ImConst {
	/**
	 * 不同协议判断方法
	 * @param imChannelContext
	 * @return
	 */
	boolean isProtocol(ImChannelContext imChannelContext);
	/**
	 * 该proCmd处理器名称(自定义)
	 * @return
	 */
	String name();
	
}
