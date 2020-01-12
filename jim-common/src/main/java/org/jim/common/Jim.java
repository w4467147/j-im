package org.jim.common;

import org.jim.common.config.ImConfig;
import org.jim.common.exception.ImException;
import org.jim.common.listener.ImUserListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Tio;
import org.tio.core.ChannelContext;
import org.tio.core.ChannelContextFilter;
import org.tio.core.TioConfig;
import org.tio.utils.lock.SetWithLock;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
/**
 * 版本: [1.0]
 * 功能说明: 
 * @author : WChao 创建时间: 2017年9月22日 上午9:07:18
 */
public class Jim {
	
	public static ImConfig imConfig = ImConfig.Global.get();
	
	private static Logger log = LoggerFactory.getLogger(Jim.class);

	/**
	 * 功能描述：[发送到群组(所有不同协议端)]
	 * @author：WChao 创建时间: 2017年9月21日 下午3:26:57
	 * @param group
	 * @param packet
	 */
	public static void sendToGroup(String group, ImPacket packet){
		Tio.sendToGroup(imConfig.getTioConfig(), group, packet);
	}
	/**
	 * 发送到指定通道;
	 * @param channelContext
	 * @param imPacket
	 */
	public static boolean send(ImChannelContext channelContext, ImPacket imPacket){
		if(channelContext == null){
			return false;
		}
		return Tio.send(channelContext.getTioChannelContext(),imPacket);
	}

	/**
	 * 阻塞发送（确认把packet发送到对端后再返回）
	 * @param channelContext
	 * @param packet
	 * @return
	 */
	public static boolean bSend(ImChannelContext channelContext , ImPacket packet){
		if(channelContext == null){
			return false;
		}
		return Tio.bSend(channelContext.getTioChannelContext(), packet);
	}
	/**
	 * 发送到指定用户;
	 * @param userId
	 * @param packet
	 */
	public static void sendToUser(String userId,ImPacket packet){
		Tio.sendToUser(imConfig.getTioConfig(), userId, packet);
	}
	/**
	 * 发送到指定ip对应的集合
	 * @param ip
	 * @param packet
	 */
	public static void sendToIp( String ip, ImPacket packet) {
		 sendToIp(ip, packet, null);
	}

	public static void sendToIp(String ip, ImPacket packet, ChannelContextFilter channelContextFilter) {
		Tio.sendToIp(imConfig.getTioConfig(), ip, packet, channelContextFilter);
	}

	public static void sendToSet(SetWithLock<ChannelContext> setWithLock, ImPacket packet, ChannelContextFilter channelContextFilter, boolean isBlock){
		Tio.sendToSet(imConfig.getTioConfig(),setWithLock,packet,channelContextFilter);
	}
	/**
	 * 绑定用户
	 * @param imChannelContext
	 * @param userId
	 */
	public static void bindUser(ImChannelContext imChannelContext,String userId){
		Tio.bindUser(imChannelContext.getTioChannelContext(), userId);
		ImUserListener imUserListener = imConfig.getImUserListener();
		if(imUserListener != null){
			try {
				imUserListener.onAfterBind(imChannelContext, userId);
			}catch (ImException e){
				log.error(e.toString(),e);
			}
		}
	}
	/**
	 * 解除userId的绑定。一般用于多地登录，踢掉前面登录的场景
	 * @param userId
	 * @author: WChao
	 */
	public static void unbindUser(String userId){
		TioConfig tioConfig = imConfig.getTioConfig();
		Tio.unbindUser(tioConfig, userId);
		ImUserListener imUserListener = imConfig.getImUserListener();
		if(imUserListener == null){
			return;
		}
		SetWithLock<ChannelContext> userChannels = Tio.getByUserid(tioConfig, userId);
		Set<ChannelContext> channelContexts = userChannels.getObj();
		if(!channelContexts.isEmpty()){
			ReadLock readLock = userChannels.getLock().readLock();
			try{
				readLock.lock();
				for (ChannelContext channelContext :  channelContexts){
					ImChannelContext imChannelContext = (ImChannelContext)channelContext.get(ImConst.Key.IM_CHANNEL_CONTEXT_KEY);
					imUserListener.onAfterUnbind(imChannelContext, userId);
				}
			} catch (ImException e) {
				log.error(e.toString(), e);
			}finally {
				readLock.unlock();
			}
		}
	}
	/**
	 * 绑定群组
	 * @param imChannelContext
	 * @param group
	 */
	public static void bindGroup(ImChannelContext imChannelContext, String group){
		Tio.bindGroup(imChannelContext.getTioChannelContext(), group);
	}
	/**
	 * 与指定组解除绑定关系
	 * @param groupId
	 * @param imChannelContext
	 * @author WChao
	 */
	public static void unbindGroup(String groupId, ImChannelContext imChannelContext){
		Tio.unbindGroup(groupId, imChannelContext.getTioChannelContext());
	}
	/**
	 * 与所有组解除解绑关系
	 * @param imChannelContext
	 * @author WChao
	 */
	public static void unbindGroup(ImChannelContext imChannelContext){
		Tio.unbindGroup(imChannelContext.getTioChannelContext());
	}
	/**
	 * 将制定用户从指定群组解除绑定
	 * @param userId
	 * @param group
	 */
	public static void unbindGroup(String userId,String group){
		Tio.unbindGroup(imConfig.getTioConfig(),userId,group);
	}
	/**
	 * 移除用户, 和close方法一样，只不过不再进行重连等维护性的操作
	 * @param userId
	 * @param remark
	 */
	public static void remove(String userId,String remark){
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

}
