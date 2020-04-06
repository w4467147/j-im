package org.jim.server.command.handler;

import org.apache.commons.collections4.CollectionUtils;
import org.jim.common.*;
import org.jim.common.config.ImConfig;
import org.jim.common.exception.ImException;
import org.jim.common.message.MessageHelper;
import org.jim.common.packets.Command;
import org.jim.common.packets.Group;
import org.jim.common.packets.LoginReqBody;
import org.jim.common.packets.LoginRespBody;
import org.jim.common.packets.User;
import org.jim.common.protocol.IProtocol;
import org.jim.common.utils.JsonKit;
import org.jim.server.ImServerChannelContext;
import org.jim.server.command.AbstractCmdHandler;
import org.jim.server.command.CommandManager;
import org.jim.server.command.handler.processor.login.LoginCmdProcessor;
import org.jim.server.handler.ProtocolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Objects;

/**
 * 登录消息命令处理器
 * @author WChao
 * @date 2018年4月10日 下午2:40:07
 */
public class LoginReqHandler extends AbstractCmdHandler {

	private static Logger log = LoggerFactory.getLogger(LoginReqHandler.class);

	@Override
	public ImPacket handler(ImPacket packet, ImChannelContext imChannelContext) throws ImException {
		ImServerChannelContext imServerChannelContext = (ImServerChannelContext)imChannelContext;
		ImSessionContext imSessionContext = imChannelContext.getSessionContext();
		LoginReqBody loginReqBody = JsonKit.toBean(packet.getBody(), LoginReqBody.class);
		LoginCmdProcessor loginProcessor = this.getSingleProcessor(LoginCmdProcessor.class);
		LoginRespBody loginRespBody = LoginRespBody.success();
		User user = null;
		if(Objects.nonNull(loginProcessor)){
			loginRespBody = loginProcessor.doLogin(loginReqBody, imChannelContext);
			if (Objects.isNull(loginRespBody) || loginRespBody.getCode() != ImStatus.C10007.getCode()) {
				log.error("login failed, userId:{}, password:{}", loginReqBody.getUserId(), loginReqBody.getPassword());
				loginProcessor.onFailed(imChannelContext);
				Jim.bSend(imChannelContext, ProtocolManager.Converter.respPacket(loginRespBody, imChannelContext));
				Jim.remove(imChannelContext, "userId or token is incorrect");
				return null;
			}
			user = loginProcessor.getUser(loginReqBody, imChannelContext);
		}
		if(Objects.isNull(user)){
			user = new User(loginReqBody.getUserId(),loginReqBody.getUserId());
		}
		IProtocol protocol = imServerChannelContext.getProtocolHandler().getProtocol();
		user.setTerminal(Objects.isNull(protocol) ? Protocol.UNKNOWN : protocol.name());
		imSessionContext.getClient().setUser(user);
		Jim.bindUser(imServerChannelContext, user.getUserId());
		//初始化绑定或者解绑群组;
		initGroup(imChannelContext, user);
		loginProcessor.onSuccess(user, imChannelContext);
		return ProtocolManager.Converter.respPacket(loginRespBody, imChannelContext);
	}

	/**
	 * 初始化绑定或者解绑群组;
	 */
	public void initGroup(ImChannelContext imChannelContext , User user)throws ImException{
		String userId = user.getUserId();
		List<Group> groups = user.getGroups();
		if(CollectionUtils.isEmpty(groups))
			return;
		boolean isStore = ImConfig.ON.equals(getImConfig().getIsStore());
		MessageHelper messageHelper = getImConfig().getMessageHelper();
		List<String> groupIds = null;
		if(isStore){
			groupIds = messageHelper.getGroups(userId);
		}
		//绑定群组
		for(Group group : groups){
			if(isStore && groupIds != null){
				groupIds.remove(group.getGroupId());
			}
			ImPacket groupPacket = new ImPacket(Command.COMMAND_JOIN_GROUP_REQ,JsonKit.toJsonBytes(group));
			try {
				JoinGroupReqHandler joinGroupReqHandler = CommandManager.getCommand(Command.COMMAND_JOIN_GROUP_REQ, JoinGroupReqHandler.class);
				joinGroupReqHandler.handler(groupPacket, imChannelContext);
			} catch (Exception e) {
				log.error(e.toString(),e);
			}
		}
		if(isStore && groupIds != null){
			for(String groupId : groupIds){
				messageHelper.getBindListener().onAfterGroupUnbind(imChannelContext, groupId);
			}
		}
	}
	@Override
	public Command command() {
		return Command.COMMAND_LOGIN_REQ;
	}
}
