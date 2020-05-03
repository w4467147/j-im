package org.jim.server.helper.redis;

import org.apache.commons.lang3.StringUtils;
import org.jim.core.ImChannelContext;
import org.jim.core.config.ImConfig;
import org.jim.core.ImSessionContext;
import org.jim.core.cache.redis.RedisCache;
import org.jim.core.cache.redis.RedisCacheManager;
import org.jim.core.exception.ImException;
import org.jim.core.listener.AbstractImStoreBindListener;
import org.jim.core.packets.Group;
import org.jim.core.packets.User;
import org.jim.server.config.ImServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 消息持久化绑定监听器
 * @author WChao
 * @date 2018年4月8日 下午4:12:31
 */
public class RedisImBindListener extends AbstractImStoreBindListener {

	private static Logger logger = LoggerFactory.getLogger(RedisImBindListener.class);

	private RedisCache groupCache;
	private RedisCache userCache;
	private final String SUFFIX = ":";
	
	public RedisImBindListener(){
		this(ImServerConfig.Global.get());
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
	public void onAfterGroupBind(ImChannelContext imChannelContext, Group group) throws ImException {
		if(!isStore()) {
			return;
		}
		initGroupUsers(group, imChannelContext);
	}

	@Override
	public void onAfterGroupUnbind(ImChannelContext imChannelContext, Group group) throws ImException {
		if(!isStore()) {
			return;
		}
		String userId = imChannelContext.getUserId();
		String groupId = group.getGroupId();
		//移除群组成员;
		groupCache.listRemove(groupId+SUFFIX+USER, userId);
		//移除成员群组;
		userCache.listRemove(userId+SUFFIX+GROUP, groupId);
		RedisCacheManager.getCache(PUSH).remove(GROUP+SUFFIX+group+SUFFIX+userId);
	}

	@Override
	public void onAfterUserBind(ImChannelContext imChannelContext, User user) throws ImException {
		if(!isStore() || Objects.isNull(user)) {
			return;
		}
		updateUserTerminal(imChannelContext, user);
		initUserInfo(user);
	}

	@Override
	public void onAfterUserUnbind(ImChannelContext imChannelContext, User user) throws ImException {
		if(!isStore()) {
			return;
		}
		
	}
	/**
	 * 初始化群组用户;
	 * @param group
	 * @param imChannelContext
	 */
	public void initGroupUsers(Group group ,ImChannelContext imChannelContext){
		String groupId = group.getGroupId();
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
		User onlineUser = imSessionContext.getImClientNode().getUser();
		if(onlineUser == null) {
			return;
		}
		List<Group> groups = onlineUser.getGroups();
		if(groups == null) {
			return;
		}
		for(Group storeGroup : groups){
			if(groupId.equals(storeGroup.getGroupId())){
				groupCache.put(groupId+SUFFIX+INFO, storeGroup);
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
	 * 更新用户终端协议类型及在线状态;
	 * @param imChannelContext
	 * @param user 更新用户信息
	 */
	private void updateUserTerminal(ImChannelContext imChannelContext , User user){
		String userId = user.getUserId();String terminal = user.getTerminal();String status = user.getStatus();
		if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(terminal) || StringUtils.isEmpty(status)) {
			logger.error("userId:{},terminal:{},status:{} must not null", userId, terminal, status);
			return;
		}
		userCache.put(userId+SUFFIX+TERMINAL+SUFFIX+terminal, user.getStatus());
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
		ImServerConfig imServerConfig = ImServerConfig.Global.get();
		return ImServerConfig.ON.equals(imServerConfig.getIsStore());
	}

}
