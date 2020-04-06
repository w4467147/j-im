package org.jim.server.helper.redis;

import org.apache.commons.lang3.StringUtils;
import org.jim.common.ImChannelContext;
import org.jim.common.config.ImConfig;
import org.jim.common.ImSessionContext;
import org.jim.common.cache.redis.RedisCache;
import org.jim.common.cache.redis.RedisCacheManager;
import org.jim.common.exception.ImException;
import org.jim.common.listener.AbstractImBindListener;
import org.jim.common.packets.Client;
import org.jim.common.packets.Group;
import org.jim.common.packets.User;
import org.jim.common.utils.ImKit;
import org.jim.server.config.ImServerConfig;
import java.io.Serializable;
import java.util.List;
/**
 * 消息持久化绑定监听器
 * @author WChao
 * @date 2018年4月8日 下午4:12:31
 */
public class RedisImBindListener extends AbstractImBindListener{
	
	private RedisCache groupCache;
	private RedisCache userCache;
	private final String SUFFIX = ":";
	
	public RedisImBindListener(){
		this(null);
	}
	
	public RedisImBindListener(ImConfig imConfig){
		this.imConfig = imConfig;
		groupCache = RedisCacheManager.getCache(GROUP);
		userCache = RedisCacheManager.getCache(USER);
	}
	
	static{
		RedisCacheManager.register(USER, Integer.MAX_VALUE, Integer.MAX_VALUE);
		RedisCacheManager.register(GROUP, Integer.MAX_VALUE, Integer.MAX_VALUE);
		RedisCacheManager.register(STORE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		RedisCacheManager.register(PUSH, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	@Override
	public void onAfterGroupBind(ImChannelContext imChannelContext, String group) throws ImException {
		if(!isStore()) {
			return;
		}
		initGroupUsers(group, imChannelContext);
	}

	@Override
	public void onAfterGroupUnbind(ImChannelContext imChannelContext, String group) throws ImException {
		if(!isStore()) {
			return;
		}
		String userId = imChannelContext.getUserId();
		//移除群组成员;
		groupCache.listRemove(group+SUFFIX+USER, userId);
		//移除成员群组;
		userCache.listRemove(userId+SUFFIX+GROUP, group);
		RedisCacheManager.getCache(PUSH).remove(GROUP+SUFFIX+group+SUFFIX+userId);
	}

	@Override
	public void onAfterUserBind(ImChannelContext imChannelContext, String userId) throws ImException {
		if(!isStore()) {
			return;
		}
		ImSessionContext imSessionContext = imChannelContext.getSessionContext();
		Client client = imSessionContext.getClient();
		if(client == null) {
			return;
		}
		User onlineUser = client.getUser();
		if(onlineUser != null){
			initUserTerminal(imChannelContext,onlineUser.getTerminal(),ONLINE);
			initUserInfo(onlineUser);
		}
	}

	@Override
	public void onAfterUserUnbind(ImChannelContext imChannelContext, String userId) throws ImException {
		if(!isStore()) {
			return;
		}
		
	}
	/**
	 * 初始化群组用户;
	 * @param groupId
	 * @param imChannelContext
	 */
	public void initGroupUsers(String groupId ,ImChannelContext imChannelContext){
		if(!isStore()) {
			return;
		}
		String userId = imChannelContext.getUserId();
		if(StringUtils.isEmpty(groupId) || StringUtils.isEmpty(userId)) {
			return;
		}
		String group_user_key = groupId+SUFFIX+USER;
		List<String> users = groupCache.listGetAll(group_user_key);
		if(!users.contains(userId)){
			groupCache.listPushTail(group_user_key, userId);
		}
		initUserGroups(userId, groupId);
		
		ImSessionContext imSessionContext = imChannelContext.getSessionContext();
		Client client = imSessionContext.getClient();
		if(client == null) {
			return;
		}
		User onlineUser = client.getUser();
		if(onlineUser == null) {
			return;
		}
		List<Group> groups = onlineUser.getGroups();
		if(groups == null) {
			return;
		}
		for(Group group : groups){
			if(groupId.equals(group.getGroupId())){
				groupCache.put(groupId+SUFFIX+INFO, group);
				break;
			}
		}
	}
	/**
	 * 初始化用户拥有哪些群组;
	 * @param userId
	 * @param group
	 */
	public void initUserGroups(String userId, String group){
		if(!isStore()) {
			return;
		}
		if(StringUtils.isEmpty(group) || StringUtils.isEmpty(userId)) {
			return;
		}
		List<String> groups = userCache.listGetAll(userId+SUFFIX+GROUP);
		if(!groups.contains(group)){
			userCache.listPushTail(userId+SUFFIX+GROUP, group);
		}
	}
	/**
	 * 初始化用户终端协议类型;
	 * @param imChannelContext
	 * @param terminal
	 * @param status(online、offline)
	 */
	@Override
	public void initUserTerminal(ImChannelContext imChannelContext , String terminal , String status){
		if(!isStore()) {
			return;
		}
		String userId = imChannelContext.getUserId();
		if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(terminal)) {
			return;
		}
		userCache.put(userId+SUFFIX+TERMINAL+SUFFIX+terminal, status);
	}
	/**
	 * 初始化用户终端协议类型;
	 * @param user
	 */
	public void initUserInfo(User user){
		if(!isStore() || user == null) {
			return;
		}
		String userId = user.getUserId();
		if(StringUtils.isEmpty(userId)) {
			return;
		}
		userCache.put(userId+SUFFIX+INFO, user.clone());
		List<Group> friends = user.getFriends();
		if(friends != null){
			userCache.put(userId+SUFFIX+FRIENDS, (Serializable) friends);
		}
	}
	/**
	 * 是否开启持久化;
	 * @return
	 */
	public boolean isStore(){
		ImServerConfig imServerConfig = (ImServerConfig)imConfig;
		return ImConfig.ON.equals(imServerConfig.getIsStore());
	}
}
