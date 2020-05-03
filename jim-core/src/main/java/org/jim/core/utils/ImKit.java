/**
 * 
 */
package org.jim.core.utils;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.jim.core.ImChannelContext;
import org.jim.core.ImSessionContext;
import org.jim.core.Jim;
import org.jim.core.packets.ImClientNode;
import org.jim.core.packets.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * IM工具类;
 * @author WChao
 *
 */
public class ImKit {
	
	private static Logger logger = LoggerFactory.getLogger(ImKit.class);

	/**
	 * 根据群组获取所有用户;
	 * @param groupId 群组ID
	 * @return 群组用户集合列表
	 */
	public static List<User> getUsersByGroup(String groupId){
		List<ImChannelContext> channelContexts = Jim.getByGroup(groupId);
		List<User> users = Lists.newArrayList();
		if(CollectionUtils.isEmpty(channelContexts)){
			return users;
		}
		Map<String,User> userMap = new HashMap<>();
		channelContexts.forEach(imChannelContext -> {
			User user = imChannelContext.getSessionContext().getImClientNode().getUser();
			if(Objects.nonNull(user) && Objects.isNull(userMap.get(user.getUserId()))){
				userMap.put(user.getUserId(), user);
				users.add(user);
			}
		});
		return users;
	}

	/**
	 * 根据用户ID获取用户信息(一个用户ID会有多端通道,默认取第一个)
	 * @param userId 用户ID
	 * @return user信息
	 */
	public static User getUser(String userId){
		List<ImChannelContext> imChannelContexts = Jim.getByUserId(userId);
		if(CollectionUtils.isEmpty(imChannelContexts)) {
			return null;
		}
		return imChannelContexts.get(0).getSessionContext().getImClientNode().getUser();
	}

	/**
	 * 设置Client对象到ImSessionContext中
	 * @param channelContext 通道上下文
	 * @return 客户端Node信息
	 * @author: WChao
	 */
	public static ImClientNode initImClientNode(ImChannelContext channelContext) {
		ImSessionContext imSessionContext = channelContext.getSessionContext();
		ImClientNode imClientNode = imSessionContext.getImClientNode();
		if(Objects.nonNull(imClientNode)){
			return imClientNode;
		}
		imClientNode = ImClientNode.newBuilder().id(channelContext.getId()).ip(channelContext.getClientNode().getIp()).port(channelContext.getClientNode().getPort()).build();
		imSessionContext.setImClientNode(imClientNode);
		return imClientNode;
	}

}
