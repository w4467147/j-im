/**
 * 
 */
package org.jim.server.handler;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jim.common.ImChannelContext;
import org.jim.common.ImConst;
import org.jim.common.ImPacket;
import org.jim.common.ImStatus;
import org.jim.common.exception.ImException;
import org.jim.common.packets.Command;
import org.jim.common.packets.RespBody;
import org.jim.common.protocol.IProtocolConverter;
import org.jim.common.utils.ImKit;
import org.jim.server.ImServerChannelContext;
import org.jim.server.config.ImServerConfig;
/**
 * 版本: [1.0]
 * 功能说明: 
 * @author : WChao 创建时间: 2017年8月3日 下午2:40:24
 */
public class ProtocolManager implements ImConst{
	
	private static Logger logger = Logger.getLogger(ProtocolManager.class);
	
	private static Map<String,AbstractProtocolHandler> serverHandlers = new HashMap<String,AbstractProtocolHandler>();
	
	static{
		try {
			List<ProtocolHandlerConfiguration> configurations = ProtocolHandlerConfigurationFactory.parseConfiguration();
			init(configurations);
		} catch (Exception e) {
			logger.error(e.toString(),e);
		}
	}
	
	private static void init(List<ProtocolHandlerConfiguration> configurations) throws Exception{
		for(ProtocolHandlerConfiguration configuration : configurations){
			Class<AbstractProtocolHandler> serverHandlerClazz = (Class<AbstractProtocolHandler>)Class.forName(configuration.getServerHandler());
			AbstractProtocolHandler serverHandler = serverHandlerClazz.newInstance();
			addServerHandler(serverHandler);
		}
	}
	
	public static AbstractProtocolHandler addServerHandler(AbstractProtocolHandler serverHandler)throws ImException{
		if(Objects.isNull(serverHandler)){
			throw new ImException("ProtocolHandler must not null ");
		}
		return serverHandlers.put(serverHandler.getProtocol().name(),serverHandler);
	}
	
	public static AbstractProtocolHandler removeServerHandler(String name)throws ImException{
		if(StringUtils.isEmpty(name)){
			throw new ImException("server name must not empty");
		}
		return serverHandlers.remove(name);
	}
	
	public static AbstractProtocolHandler initProtocolHandler(ByteBuffer buffer, ImChannelContext imChannelContext){
		ImServerChannelContext imServerChannelContext = (ImServerChannelContext)imChannelContext;
		for(Entry<String,AbstractProtocolHandler> entry : serverHandlers.entrySet()){
			AbstractProtocolHandler protocolHandler = entry.getValue();
			try {
				if(protocolHandler.getProtocol().isProtocol(buffer, imServerChannelContext)){
					imServerChannelContext.setProtocolHandler(protocolHandler);
					return protocolHandler;
				}
			} catch (Throwable e) {
				logger.error(e);
			}
		}
		return null;
	}
	
	public static <T> T getServerHandler(String name,Class<T> clazz){
		AbstractProtocolHandler serverHandler = serverHandlers.get(name);
		if(Objects.isNull(serverHandler)) {
			return null;
		}
		return (T)serverHandler;
	}

	public static void init(){
		init((ImServerConfig)ImServerConfig.Global.get());
	}
	public static void init(ImServerConfig imServerConfig){
		for(Entry<String,AbstractProtocolHandler> entry : serverHandlers.entrySet()){
			try {
				entry.getValue().init(imServerConfig);
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	public static class Converter{

		/**
		 * 功能描述：[转换不同协议响应包]
		 * @author：WChao 创建时间: 2017年9月21日 下午3:21:54
		 * @param respBody
		 * @param channelContext
		 * @return
		 *
		 */
		public static ImPacket respPacket(RespBody respBody, ImChannelContext channelContext)throws ImException {
			if(Objects.isNull(respBody)) {
				throw new ImException("响应包体不能为空!");
			}
			return respPacket(respBody.toByte(), respBody.getCommand(), channelContext);
		}

		/**
		 * 功能描述：[转换不同协议响应包]
		 * @author：WChao 创建时间: 2017年9月21日 下午3:21:54
		 * @param body
		 * @param channelContext
		 * @return
		 *
		 */
		public static ImPacket respPacket(byte[] body, Command command, ImChannelContext channelContext)throws ImException{
			ImServerChannelContext serverChannelContext = (ImServerChannelContext)channelContext;
			AbstractProtocolHandler protocolHandler = serverChannelContext.getProtocolHandler();
			if(Objects.isNull(protocolHandler)){
				throw new ImException("协议[ProtocolHandler]未初始化,协议包转化失败");
			}
			IProtocolConverter converter = protocolHandler.getProtocol().getConverter();
			if(converter != null){
				return converter.RespPacket(body, command, channelContext);
			}else {
				throw new ImException("未获取到协议转化器[ProtocolConverter]");
			}
		}

		public static ImPacket respPacket(ImPacket imPacket, ImChannelContext channelContext)throws ImException{
			return respPacket(imPacket, imPacket.getCommand(), channelContext);
		}

		public static ImPacket respPacket(ImPacket imPacket,Command command, ImChannelContext channelContext)throws ImException{
			return respPacket(imPacket.getBody(), command, channelContext);
		}

	}

	public static class Packet{
		/**
		 * 数据格式不正确响应包
		 * @param imChannelContext
		 * @return imPacket
		 * @throws ImException
		 */
		public static ImPacket  dataInCorrect(ImChannelContext imChannelContext) throws ImException{
			RespBody chatDataInCorrectRespPacket = new RespBody(Command.COMMAND_CHAT_RESP,ImStatus.C10002);
			ImPacket respPacket = Converter.respPacket(chatDataInCorrectRespPacket, imChannelContext);
			respPacket.setStatus(ImStatus.C10002);
			return respPacket;
		}

		/**
		 * 发送成功响应包
		 * @param imChannelContext
		 * @return imPacket
		 * @throws ImException
		 */
		public static ImPacket  success(ImChannelContext imChannelContext) throws ImException{
			RespBody chatDataInCorrectRespPacket = new RespBody(Command.COMMAND_CHAT_RESP, ImStatus.C10000);
			ImPacket respPacket = Converter.respPacket(chatDataInCorrectRespPacket, imChannelContext);
			respPacket.setStatus(ImStatus.C10000);
			return respPacket;
		}

		/**
		 * 用户不在线响应包
		 * @param imChannelContext
		 * @return
		 * @throws ImException
		 */
		public static ImPacket  offline(ImChannelContext imChannelContext) throws ImException{
			RespBody chatDataInCorrectRespPacket = new RespBody(Command.COMMAND_CHAT_RESP,ImStatus.C10001);
			ImPacket respPacket = Converter.respPacket(chatDataInCorrectRespPacket, imChannelContext);
			respPacket.setStatus(ImStatus.C10001);
			return respPacket;
		}
	}
}
