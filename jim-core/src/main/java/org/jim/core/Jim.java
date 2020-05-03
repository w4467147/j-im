package org.jim.core;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jim.core.cluster.ImCluster;
import org.jim.core.config.ImConfig;
import org.jim.core.exception.ImException;
import org.jim.core.listener.ImGroupListener;
import org.jim.core.listener.ImUserListener;
import org.jim.core.packets.Group;
import org.jim.core.packets.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Tio;
import org.tio.core.ChannelContext;
import org.tio.core.ChannelContextFilter;
import org.tio.core.TioConfig;
import org.tio.utils.lock.SetWithLock;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
/**
 * 版本: [1.0]
 * 功能说明: JIM
 * @author : WChao 创建时间: 2017年9月22日 上午9:07:18
 */
public class Jim implements ImConst{
	
	public static ImConfig imConfig = ImConfig.Global.get();
	
	private static Logger log = LoggerFactory.getLogger(Jim.class);

	/**
	 * 根据群组ID获取该群组下所有Channel
	 * @param groupId 群组ID
	 * @return 群组下所有通道集合
	 */
	public static List<ImChannelContext> getByGroup(String groupId){
		SetWithLock<ChannelContext> channelContextSetWithLock = Tio.getByGroup(imConfig.getTioConfig(), groupId);
		return convertChannelToImChannel(channelContextSetWithLock);
	}

	/**
	 * 根据用户ID获取用户下所有Channel
	 * @param userId 用户ID
	 * @return 用户所有通道集合
	 */
	public static List<ImChannelContext> getByUserId(String userId){
		SetWithLock<ChannelContext> channelContextSetWithLock = Tio.getByUserid(imConfig.getTioConfig(), userId);
		return convertChannelToImChannel(channelContextSetWithLock);
	}

	/**
	 * 功能描述：[发送到群组(所有不同协议端)]
	 * @param groupId 群组ID
	 * @param packet 消息包
	 */
	public static void sendToGroup(String groupId, ImPacket packet){
		try {
			Tio.sendToGroup(imConfig.getTioConfig(), groupId, packet);
		}finally {
			ImCluster cluster = imConfig.getCluster();
			if (Objects.nonNull(cluster) && !packet.isFromCluster()) {
				cluster.clusterToGroup(groupId, packet);
			}
		}
	}

	/**
	 * 发送到指定通道;
	 * @param imChannelContext IM通道上下文
	 * @param imPacket 消息包
	 */
	public static boolean send(ImChannelContext imChannelContext, ImPacket imPacket){
		if(imChannelContext == null){
			return false;
		}
		return Tio.send(imChannelContext.getTioChannelContext(),imPacket);
	}

	/**
	 * 阻塞发送（确认把packet发送到对端后再返回）
	 * @param imChannelContext IM通道上下文
	 * @param packet 消息包
	 * @return
	 */
	public static boolean bSend(ImChannelContext imChannelContext , ImPacket packet){
		if(imChannelContext == null){
			return false;
		}
		return Tio.bSend(imChannelContext.getTioChannelContext(), packet);
	}

	/**
	 * 发送到指定用户;
	 * @param userId 用户ID
	 * @param packet 消息包
	 */
	public static void sendToUser(String userId,ImPacket packet){
		try {
			Tio.sendToUser(imConfig.getTioConfig(), userId, packet);
		}finally {
			ImCluster cluster = imConfig.getCluster();
			if (Objects.nonNull(cluster) && !packet.isFromCluster()) {
				cluster.clusterToUser(userId, packet);
			}
		}
	}

	/**
	 * 发送到指定ip对应的集合
	 * @param ip 客户端IP地址
	 * @param packet 消息包
	 */
	public static void sendToIp( String ip, ImPacket packet) {
		 sendToIp(ip, packet, null);
	}

	/**
	 * 发送到指定ip对应的集合
	 * @param ip 客户端IP地址
	 * @param packet 消息包
	 * @param channelContextFilter 通道过滤器
	 */
	public static void sendToIp(String ip, ImPacket packet, ChannelContextFilter channelContextFilter) {
		try{
			Tio.sendToIp(imConfig.getTioConfig(), ip, packet, channelContextFilter);
		}finally {
			ImCluster cluster = imConfig.getCluster();
			if (Objects.nonNull(cluster) && !packet.isFromCluster()) {
				cluster.clusterToIp(ip, packet);
			}
		}
	}

	/**
	 * 绑定用户(如果配置了回调函数执行回调)
	 * @param imChannelContext IM通道上下文
	 * @param userId 用户ID
	 */
	public static boolean bindUser(ImChannelContext imChannelContext, String userId){
		User user = imChannelContext.getSessionContext().getImClientNode().getUser();
		if(Objects.isNull(user)){
			user = User.newBuilder().userId(userId).status(ONLINE).build();
		}
		return bindUser(imChannelContext, user);
	}

	/**
	 * 绑定用户(如果配置了回调函数执行回调)
	 * @param imChannelContext IM通道上下文
	 * @param user 绑定用户信息
	 */
	public static boolean bindUser(ImChannelContext imChannelContext, User user){
		String userId = user.getUserId();
		if(StringUtils.isEmpty(userId)){
			log.error("userId is null");
			return false;
		}
		Tio.bindUser(imChannelContext.getTioChannelContext(), userId);
		SetWithLock<ChannelContext> channelContextSetWithLock = Tio.getByUserid(imConfig.getTioConfig(), userId);
		ReadLock lock = channelContextSetWithLock.getLock().readLock();
		try {
			lock.lock();
			if(CollectionUtils.isEmpty(channelContextSetWithLock.getObj())){
				return false;
			}
			imChannelContext.getSessionContext().getImClientNode().setUser(user);
			ImUserListener imUserListener = imConfig.getImUserListener();
			if(Objects.nonNull(imUserListener)){
				imUserListener.onAfterBind(imChannelContext, user);
			}
		}catch (ImException e) {
			log.error(e.getMessage(), e);
			return false;
		}finally {
			lock.unlock();
		}
		return true;
	}

	/**
	 * 解除userId的绑定。一般用于多地登录，踢掉前面登录的场景
	 * @param userId 解绑用户ID
	 * @author: WChao
	 */
	public static boolean unbindUser(String userId){
		return unbindUser(User.newBuilder().userId(userId).build());
	}

	/**
	 * 解除userId的绑定。一般用于多地登录，踢掉前面登录的场景
	 * @param user 解绑用户信息
	 * @author: WChao
	 */
	public static boolean unbindUser(User user){
		String userId = user.getUserId();
		if(StringUtils.isEmpty(userId)){
			log.error("userId is null");
			return false;
		}
		TioConfig tioConfig = imConfig.getTioConfig();
		SetWithLock<ChannelContext> userChannels = Tio.getByUserid(tioConfig, userId);
		Set<ChannelContext> channelContexts = userChannels.getObj();
		if(channelContexts.isEmpty()){
			return true;
		}
		ReadLock readLock = userChannels.getLock().readLock();
		try{
			readLock.lock();
			for (ChannelContext channelContext :  channelContexts){
				ImChannelContext imChannelContext = (ImChannelContext)channelContext.get(ImConst.Key.IM_CHANNEL_CONTEXT_KEY);
				ImUserListener imUserListener = imConfig.getImUserListener();
				if(Objects.isNull(imUserListener))continue;
				User existUser = imChannelContext.getSessionContext().getImClientNode().getUser();
				imUserListener.onAfterUnbind(imChannelContext, existUser);
			}
			Tio.unbindUser(tioConfig, userId);
		} catch (ImException e) {
			log.error("unbind user failed", e);
			return false;
		}finally {
			readLock.unlock();
		}
		return true;
	}
	/**
	 * 绑定群组(如果配置了群组监听器,执行回调)
	 * @param imChannelContext IM通道上下文
	 * @param groupId 绑定群组ID
	 */
	public static boolean bindGroup(ImChannelContext imChannelContext, String groupId){
		return bindGroup(imChannelContext, Group.newBuilder().groupId(groupId).build());
	}

	/**
	 * 绑定群组(如果配置了群组监听器,执行回调)
	 * @param imChannelContext IM通道上下文
	 * @param group 绑定群组对象
	 */
	public static boolean bindGroup(ImChannelContext imChannelContext, Group group){
		String groupId = group.getGroupId();
		if(StringUtils.isEmpty(groupId)){
			log.error("groupId is null");
			return false;
		}
		Tio.bindGroup(imChannelContext.getTioChannelContext(), group.getGroupId());
		return true;
	}

	/**
	 * 与指定组解除绑定关系
	 * @param groupId
	 * @param imChannelContext
	 * @author WChao
	 */
	public static boolean unbindGroup(String groupId, ImChannelContext imChannelContext){
		if(StringUtils.isEmpty(groupId)){
			log.error("groupId is null");
			return false;
		}
		Tio.unbindGroup(groupId, imChannelContext.getTioChannelContext());
		return true;
	}

	/**
	 * 与所有组解除解绑关系
	 * @param imChannelContext
	 * @author WChao
	 */
	public static boolean unbindGroup(ImChannelContext imChannelContext){
		Tio.unbindGroup(imChannelContext.getTioChannelContext());
		return true;
	}

	/**
	 * 将指定用户从指定群组解除绑定
	 * @param userId 用户ID
	 * @param groupId 群组ID
	 */
	public static boolean unbindGroup(String userId, String groupId){
		if(StringUtils.isEmpty(groupId) || StringUtils.isEmpty(userId)){
			log.error("groupId or userId is null");
			return false;
		}
		Tio.unbindGroup(imConfig.getTioConfig(), userId, groupId);
		return true;
	}

	/**
	 * 移除用户, 和close方法一样，只不过不再进行重连等维护性的操作
	 * @param userId 用户ID
	 * @param remark 移除原因描述
	 */
	public static void remove(String userId, String remark){
		SetWithLock<ChannelContext> userChannelContexts = Tio.getByUserid(imConfig.getTioConfig(), userId);
		Set<ChannelContext> channels = userChannelContexts.getObj();
		if(channels.isEmpty()){
			return;
		}
		ReadLock readLock = userChannelContexts.getLock().readLock();
		try{
			readLock.lock();
			for(ChannelContext channelContext : channels){
				ImChannelContext imChannelContext = (ImChannelContext)channelContext.get(ImConst.Key.IM_CHANNEL_CONTEXT_KEY);
				remove(imChannelContext, remark);
			}
		}finally{
			readLock.unlock();
		}
	}

	/**
	 * 移除指定channel, 和close方法一样，只不过不再进行重连等维护性的操作
	 * @param imChannelContext
	 * @param remark
	 */
	public static void remove(ImChannelContext imChannelContext, String remark){
		Tio.remove(imChannelContext.getTioChannelContext(), remark);
	}

	/**
	 * 关闭连接
	 * @param imChannelContext
	 * @param remark
	 */
	public static void close(ImChannelContext imChannelContext, String remark){
		Tio.close(imChannelContext.getTioChannelContext(), remark);
	}

	/**
	 * 转换Channel为ImChannel
	 * @param channelContextSetWithLock channel上下文对象
	 * @return
	 */
	private static List<ImChannelContext> convertChannelToImChannel(SetWithLock<ChannelContext> channelContextSetWithLock){
		List<ImChannelContext> imChannelContexts = Lists.newArrayList();
		if(Objects.isNull(channelContextSetWithLock)){
			return imChannelContexts;
		}
		ReadLock lock = channelContextSetWithLock.getLock().readLock();
		try {
			lock.lock();
			Set<ChannelContext> channelContexts = channelContextSetWithLock.getObj();
			if(CollectionUtils.isEmpty(channelContexts)){
				return imChannelContexts;
			}
			for(ChannelContext channelContext : channelContexts){
				ImChannelContext imChannelContext = (ImChannelContext)channelContext.get(Key.IM_CHANNEL_CONTEXT_KEY);
				imChannelContexts.add(imChannelContext);
			}
			return imChannelContexts;
		}catch (Exception e){
			log.error(e.getMessage(), e);
		}finally {
			lock.unlock();
		}
		return imChannelContexts;
	}

}
