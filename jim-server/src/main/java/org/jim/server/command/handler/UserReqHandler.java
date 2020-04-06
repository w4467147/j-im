/**
 * 
 */
package org.jim.server.command.handler;

import org.apache.commons.lang3.StringUtils;
import org.jim.common.*;
import org.jim.common.config.ImConfig;
import org.jim.common.exception.ImException;
import org.jim.common.message.MessageHelper;
import org.jim.common.packets.*;
import org.jim.common.utils.ImKit;
import org.jim.common.utils.JsonKit;
import org.jim.server.command.AbstractCmdHandler;
import org.jim.server.command.handler.userInfo.IUserInfo;
import org.jim.server.command.handler.userInfo.NonPersistentUserInfo;
import org.jim.server.command.handler.userInfo.PersistentUserInfo;
import org.jim.server.handler.ProtocolManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 版本: [1.0]
 * 功能说明: 获取用户信息消息命令
 * @author : WChao 创建时间: 2017年9月18日 下午4:08:47
 */
public class UserReqHandler extends AbstractCmdHandler {

	private IUserInfo nonPersistentUserInfo;
	private IUserInfo persistentUserInfo;

	public UserReqHandler(){
		persistentUserInfo = new PersistentUserInfo();
		nonPersistentUserInfo = new NonPersistentUserInfo();
	}
	@Override
	public Command command() {
		return Command.COMMAND_GET_USER_REQ;
	}

	@Override
	public ImPacket handler(ImPacket packet, ImChannelContext imChannelContext) throws ImException {
		UserReqBody userReqBody = JsonKit.toBean(packet.getBody(),UserReqBody.class);
		String userId = userReqBody.getUserId();
		if(StringUtils.isEmpty(userId)) {
			return ProtocolManager.Converter.respPacket(new RespBody(Command.COMMAND_GET_USER_RESP, ImStatus.C10004), imChannelContext);
		}
		//(0:所有在线用户,1:所有离线用户,2:所有用户[在线+离线]);
		Integer type = userReqBody.getType() == null ? UserStatusType.ALL.getNumber() : userReqBody.getType();
		if(Objects.isNull(UserStatusType.valueOf(type))){
			return ProtocolManager.Converter.respPacket(new RespBody(Command.COMMAND_GET_USER_RESP, ImStatus.C10004), imChannelContext);
		}
		RespBody resPacket = new RespBody(Command.COMMAND_GET_USER_RESP);
		//是否开启持久化;
		boolean isStore = ImConfig.ON.equals(getImConfig().getIsStore());
		if(isStore){
			resPacket.setData(persistentUserInfo.getUserInfo(userReqBody, imChannelContext));
		}else {
			resPacket.setData(nonPersistentUserInfo.getUserInfo(userReqBody, imChannelContext));
		}
		//在线用户
		if(UserStatusType.ONLINE.getNumber() == userReqBody.getType()){
			resPacket.setCode(ImStatus.C10005.getCode()).setMsg(ImStatus.C10005.getMsg());
			//离线用户;
		}else if(UserStatusType.OFFLINE.getNumber() == userReqBody.getType()){
			resPacket.setCode(ImStatus.C10006.getCode()).setMsg(ImStatus.C10006.getMsg());
			//在线+离线用户;
		}else if(UserStatusType.ALL.getNumber() == userReqBody.getType()){
			resPacket.setCode(ImStatus.C10003.getCode()).setMsg(ImStatus.C10003.getMsg());
		}
		return ProtocolManager.Converter.respPacket(resPacket, imChannelContext);
	}

	public IUserInfo getNonPersistentUserInfo() {
		return nonPersistentUserInfo;
	}

	public void setNonPersistentUserInfo(IUserInfo nonPersistentUserInfo) {
		this.nonPersistentUserInfo = nonPersistentUserInfo;
	}

	public IUserInfo getPersistentUserInfo() {
		return persistentUserInfo;
	}

	public void setPersistentUserInfo(IUserInfo persistentUserInfo) {
		this.persistentUserInfo = persistentUserInfo;
	}

}
