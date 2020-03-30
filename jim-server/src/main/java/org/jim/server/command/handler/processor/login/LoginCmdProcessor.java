/**
 * 
 */
package org.jim.server.command.handler.processor.login;

import org.jim.common.ImChannelContext;
import org.jim.common.packets.LoginReqBody;
import org.jim.common.packets.LoginRespBody;
import org.jim.server.command.handler.processor.SingleProtocolCmdProcessor;
/**
 *
 * @author WChao
 */
public interface LoginCmdProcessor extends SingleProtocolCmdProcessor {
	/**
	 * 执行登录操作接口方法
	 * @param loginReqBody
	 * @param imChannelContext
	 * @return
	 */
	 LoginRespBody doLogin(LoginReqBody loginReqBody , ImChannelContext imChannelContext);

	/**
	 * 登录成功回调方法
	 * @param imChannelContext
	 */
	 void onSuccess(ImChannelContext imChannelContext);
}
