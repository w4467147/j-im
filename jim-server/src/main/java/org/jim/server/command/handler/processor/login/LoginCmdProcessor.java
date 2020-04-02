/**
 * 
 */
package org.jim.server.command.handler.processor.login;

import org.jim.common.ImChannelContext;
import org.jim.common.packets.LoginReqBody;
import org.jim.common.packets.LoginRespBody;
import org.jim.common.packets.User;
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
	 LoginRespBody doLogin(LoginReqBody loginReqBody, ImChannelContext imChannelContext);
	/**
	 * 获取用户信息接口方法
	 * @param loginReqBody
	 * @param imChannelContext
	 * @return
	 */
	User getUser(LoginReqBody loginReqBody, ImChannelContext imChannelContext);
	/**
	 * 登录成功回调方法
	 * @param imChannelContext
	 */
	 void onSuccess(User user, ImChannelContext imChannelContext);

	/**
	 * 登陆失败回调方法
	 * @param imChannelContext
	 */
	 void onFailed(ImChannelContext imChannelContext);
}
