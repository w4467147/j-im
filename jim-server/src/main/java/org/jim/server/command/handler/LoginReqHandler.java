package org.jim.server.command.handler;

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
		if(Objects.isNull(packet.getBody())){
			Jim.bSend(imChannelContext, ProtocolManager.Converter.respPacket(LoginRespBody.failed("body must not null!"),imChannelContext));
			Jim.remove(imChannelContext, "body is null!");
			return null;
		}
		ImServerChannelContext imServerChannelContext = (ImServerChannelContext)imChannelContext;
		ImSessionContext imSessionContext = imChannelContext.getSessionContext();
		LoginReqBody loginReqBody = JsonKit.toBean(packet.getBody(), LoginReqBody.class);
		LoginCmdProcessor loginProcessor = this.getSingleProcessor(LoginCmdProcessor.class);
		LoginRespBody loginRespBody = null;
		User user = null;
		if(Objects.nonNull(loginProcessor)){
			loginRespBody = loginProcessor.doLogin(loginReqBody, imChannelContext);
			if (Objects.isNull(loginRespBody) || loginRespBody.getCode() != ImStatus.C10007.getCode()) {
				log.warn("login failed, userId:{}, password:{}", loginReqBody.getUserId(), loginReqBody.getPassword());
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
		bindUnbindGroup(imChannelContext, user);
		loginProcessor.onSuccess(user, imChannelContext);
		return ProtocolManager.Converter.respPacket(loginRespBody, imChannelContext);
	}

	/**
	 * 初始化绑定或者解绑群组;
	 */
	public void bindUnbindGroup(ImChannelContext imChannelContext , User user)throws ImException{
		String userId = user.getUserId();
		List<Group> groups = user.getGroups();
		if( groups != null){
			boolean isStore = ImConfig.Const.ON.equals(getImConfig().getIsStore());
			MessageHelper messageHelper = null;
			List<String> groupIds = null;
			if(isStore){
				messageHelper = getImConfig().getMessageHelper();
				groupIds = messageHelper.getGroups(userId);
			}
			//绑定群组
			for(Group group : groups){
				if(isStore && groupIds != null){
					groupIds.remove(group.getGroup_id());
				}
				ImPacket groupPacket = new ImPacket(Command.COMMAND_JOIN_GROUP_REQ,JsonKit.toJsonBytes(group));
				try {
					JoinGroupReqHandler joinGroupReqHandler = CommandManager.getCommand(Command.COMMAND_JOIN_GROUP_REQ, JoinGroupReqHandler.class);
					joinGroupReqHandler.bindGroup(groupPacket, imChannelContext);
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
	}
	@Override
	public Command command() {
		return Command.COMMAND_LOGIN_REQ;
	}
}
