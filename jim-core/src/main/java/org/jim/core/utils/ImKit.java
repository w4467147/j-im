/**
 * 
 */
package org.jim.core.utils;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.jim.core.ImChannelContext;
import org.jim.core.Jim;
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
	 * @return
	 */
	public static List<User> getUsersByGroup(String groupId){
		List<ImChannelContext> channelContexts = Jim.getByGroup(groupId);
		List<User> users = Lists.newArrayList();
		if(CollectionUtils.isEmpty(channelContexts)){
			return users;
		}
		Map<String,User> userMap = new HashMap<>();
		for(ImChannelContext channelContext : channelContexts){
			User user = channelContext.getSessionContext().getClient().getUser();
			if(Objects.nonNull(user) && userMap.get(user.getUserId()) == null){
				userMap.put(user.getUserId(), user);
				users.add(user);
			}
		}
		return users;
	}

	/**
	 * 根据用户ID获取用户信息
	 * @param userId 用户ID
	 * @return
	 */
	public static User getUser(String userId){
		List<ImChannelContext> imChannelContexts = Jim.getByUserId(userId);
		if(CollectionUtils.isEmpty(imChannelContexts)) {
			return null;
		}
		User user = null;
		for(ImChannelContext channelContext : imChannelContexts){
			user =  channelContext.getSessionContext().getClient().getUser();
			break;
		}
		return user;
	}

}
