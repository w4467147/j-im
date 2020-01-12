package org.jim.server.listener;

import org.jim.common.ImChannelContext;
import org.jim.common.ImConst;
import org.jim.common.ImPacket;
import org.jim.server.ImServerChannelContext;
import org.jim.server.command.handler.processor.chat.MsgQueueRunnable;
import org.jim.server.config.ImServerConfig;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioListener;
/**
 *
 * @author WChao
 *
 */
public class ImServerListenerAdapter implements ServerAioListener, ImConst{

	private ImServerListener imServerListener;

	/**
	 * @author: WChao
	 * 2016年12月16日 下午5:52:06
	 * 
	 */
	public ImServerListenerAdapter(ImServerListener imServerListener) {
		this.imServerListener = imServerListener == null ? new DefaultImServerListener(): imServerListener;
	}
	/**
	 * 
	 * 建链后触发本方法，注：建链不一定成功，需要关注参数isConnected
	 * @param channelContext
	 * @param isConnected 是否连接成功,true:表示连接成功，false:表示连接失败
	 * @param isReconnect 是否是重连, true: 表示这是重新连接，false: 表示这是第一次连接
	 * @throws Exception
	 * @author: WChao
	 */
	@Override
	public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect)throws Exception{
		ImServerChannelContext imChannelContext = new ImServerChannelContext(ImServerConfig.Global.get(),channelContext);
		channelContext.set(Key.IM_CHANNEL_CONTEXT_KEY, imChannelContext);
		imChannelContext.setMsgQue(new MsgQueueRunnable(imChannelContext, imChannelContext.getImConfig().getJimExecutor()));
		imServerListener.onAfterConnected(imChannelContext, isConnected, isReconnect);
	}

	/**
	 * 消息包发送之后触发本方法
	 * @param channelContext
	 * @param packet
	 * @param isSentSuccess true:发送成功，false:发送失败
	 * @throws Exception
	 * @author WChao
	 */
	@Override
	public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess)throws Exception{
		imServerListener.onAfterSent((ImChannelContext)channelContext.get(Key.IM_CHANNEL_CONTEXT_KEY), (ImPacket)packet, isSentSuccess);
	}
	/**
	 * 连接关闭前触发本方法
	 * @param channelContext the channelContext
	 * @param throwable the throwable 有可能为空
	 * @param remark the remark 有可能为空
	 * @param isRemove
	 * @author WChao
	 * @throws Exception 
	 */
	@Override
	public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove)throws Exception{
		imServerListener.onBeforeClose((ImChannelContext)channelContext.get(Key.IM_CHANNEL_CONTEXT_KEY), throwable, remark, isRemove);
		/*if (imConfig == null) {
			return;
		}
		MessageHelper messageHelper = imConfig.getMessageHelper();
		if(messageHelper != null){
			ImSessionContext imSessionContext = (ImSessionContext)channelContext.getAttribute();
			if(imSessionContext == null) {
				return;
			}
			Client client = imSessionContext.getClient();
			if(client == null) {
				return;
			}
			User onlineUser = client.getUser();
			if(onlineUser == null) {
				return;
			}
			messageHelper.getBindListener().initUserTerminal(channelContext, onlineUser.getTerminal(), ImConst.OFFLINE);
		}*/
	}
	/**
	 * 解码成功后触发本方法
	 * @param channelContext
	 * @param packet
	 * @param packetSize
	 * @throws Exception
	 * @author: WChao
	 */
	@Override
	public void onAfterDecoded(ChannelContext channelContext, Packet packet,int packetSize) throws Exception {
		imServerListener.onAfterDecoded((ImChannelContext)channelContext.get(Key.IM_CHANNEL_CONTEXT_KEY), (ImPacket)packet, packetSize);
	}
	/**
	 * 接收到TCP层传过来的数据后
	 * @param channelContext
	 * @param receivedBytes 本次接收了多少字节
	 * @throws Exception
	 */
	@Override
	public void onAfterReceivedBytes(ChannelContext channelContext,int receivedBytes) throws Exception {
		imServerListener.onAfterReceivedBytes((ImChannelContext)channelContext.get(Key.IM_CHANNEL_CONTEXT_KEY), receivedBytes);
	}
	/**
	 * 处理一个消息包后
	 * @param channelContext
	 * @param packet
	 * @param cost 本次处理消息耗时，单位：毫秒
	 * @throws Exception
	 */
	@Override
	public void onAfterHandled(ChannelContext channelContext, Packet packet,long cost) throws Exception {
		imServerListener.onAfterHandled((ImChannelContext)channelContext.get(Key.IM_CHANNEL_CONTEXT_KEY), (ImPacket)packet, cost);
	}

	@Override
	public boolean onHeartbeatTimeout(ChannelContext channelContext, Long aLong, int i) {
		return imServerListener.onHeartbeatTimeout((ImChannelContext)channelContext.get(Key.IM_CHANNEL_CONTEXT_KEY), aLong, i);
	}
}
