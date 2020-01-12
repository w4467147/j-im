package org.jim.common.listener;

import org.jim.common.ImChannelContext;
import org.jim.common.exception.ImException;

/**
 * IM绑定用户及群组监听器;
 * @author WChao
 * @date 2018年4月8日 下午4:09:14
 */
public interface ImBindListener {
	/**
	 * 绑定群组后回调该方法
	 * @param imChannelContext
	 * @param group
	 * @throws Exception
	 */
	void onAfterGroupBind(ImChannelContext imChannelContext, String group) throws ImException;

	/**
	 * 解绑群组后回调该方法
	 * @param imChannelContext
	 * @param group
	 * @throws Exception
	 */
	void onAfterGroupUnbind(ImChannelContext imChannelContext, String group) throws ImException;
	/**
	 * 绑定用户后回调该方法
	 * @param imChannelContext
	 * @param userId
	 * @throws Exception
	 */
	void onAfterUserBind(ImChannelContext imChannelContext, String userId) throws ImException;

	/**
	 * 解绑用户后回调该方法
	 * @param imChannelContext
	 * @param userId
	 * @throws Exception
	 */
	void onAfterUserUnbind(ImChannelContext imChannelContext, String userId) throws Exception;
	/**
	 * 更新用户终端协议类型及在线状态;
	 * @param imChannelContext
	 * @param terminal(ws、tcp、http、android、ios等)
	 * @param status(online、offline)
	 */
    void initUserTerminal(ImChannelContext imChannelContext , String terminal , String status);
}
