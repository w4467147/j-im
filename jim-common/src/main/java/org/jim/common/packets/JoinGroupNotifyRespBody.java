/**
 * 
 */
package org.jim.common.packets;

import org.jim.common.ImStatus;
import org.jim.common.Status;

/**
 * 版本: [1.0]
 * 功能说明: 进入群组通知消息体
 * 作者: WChao 创建时间: 2017年7月26日 下午5:14:04
 */
public class JoinGroupNotifyRespBody extends RespBody{
	
	private static final long serialVersionUID = 3828976681110713803L;
	private User user;
	private String group;

	public JoinGroupNotifyRespBody(Command command, Status status){
		super(command,status);
	}
	public User getUser() {
		return user;
	}
	public JoinGroupNotifyRespBody setUser(User user) {
		this.user = user;
		return this;
	}
	public String getGroup() {
		return group;
	}
	public JoinGroupNotifyRespBody setGroup(String group) {
		this.group = group;
		return this;
	}
}
