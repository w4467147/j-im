/**
 * 
 */
package org.jim.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jim.common.*;
import org.jim.common.config.ImConfig;
import org.jim.common.exception.ImException;
import org.jim.common.http.HttpConst;
import org.jim.common.packets.ChatBody;
import org.jim.common.packets.Command;
import org.jim.common.packets.RespBody;
import org.jim.common.packets.User;
import org.jim.common.session.id.impl.UUIDSessionIdGenerator;
import org.tio.core.ChannelContext;

/**
 * IM聊天命令工具类
 * @date 2018-09-05 23:29:30
 * @author WChao
 *
 */
public class ChatKit {
	
	private static Logger log = Logger.getLogger(ChatKit.class);

	/**
	 * 转换为聊天消息结构;
	 * @param body
	 * @param imChannelContext
	 * @return
	 */
	public static ChatBody toChatBody(byte[] body,ImChannelContext imChannelContext){
		ChatBody chatReqBody = parseChatBody(body);
		if(chatReqBody != null){
			if(StringUtils.isEmpty(chatReqBody.getFrom())){
				ImSessionContext imSessionContext = imChannelContext.getSessionContext();
				User user = imSessionContext.getClient().getUser();
				if(user != null){
					chatReqBody.setFrom(user.getNick());
				}else{
					chatReqBody.setFrom(imChannelContext.getId());
				}
			}
		}
		return chatReqBody;
	}

	/**
	 * 判断是否属于指定格式聊天消息;
	 * @param body
	 * @return
	 */
	private static ChatBody parseChatBody(byte[] body){
		if(body == null) {
			return null;
		}
		ChatBody chatReqBody = null;
		try{
			String text = new String(body,ImConst.CHARSET);
		    chatReqBody = JsonKit.toBean(text,ChatBody.class);
			if(chatReqBody != null){
				if(chatReqBody.getCreateTime() == null) {
					chatReqBody.setCreateTime(System.currentTimeMillis());
				}
				if(StringUtils.isEmpty(chatReqBody.getId())){
					chatReqBody.setId(UUIDSessionIdGenerator.instance.sessionId(null));
				}
				return chatReqBody;
			}
		}catch(Exception e){
			log.error(e.toString());
		}
		return chatReqBody;
	}

	/**
	 * 判断是否属于指定格式聊天消息;
	 * @param bodyStr
	 * @return
	 */
	public static ChatBody parseChatBody(String bodyStr){
		if(bodyStr == null) {
			return null;
		}
		try {
			return parseChatBody(bodyStr.getBytes(ImConst.CHARSET));
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}

     /**
      * 判断用户是否在线
      * @param userId
	  * @param imConfig
      * @return
      */
     public static boolean isOnline(String userId , ImConfig imConfig){
    	/* boolean isStore = ImConst.ON.equals(imConfig.getIsStore());
		 if(isStore){
			return imConfig.getMessageHelper().isOnline(userId);
		 }
    	 SetWithLock<ChannelContext> toChannelContexts = Jim.getChannelContextsByUserId(userId);
    	 if(toChannelContexts != null && toChannelContexts.size() > 0){
    		 return true;
    	 }*/
    	 return false;
     }

	/**
	 * 获取双方会话ID
	 *
	 * @param from
	 * @param to
	 * @return
	 */
	public static String sessionId(String from, String to) {
		if (from.compareTo(to) <= 0) {
			return from + "-" + to;
		} else {
			return to + "-" + from;
		}
	}
}
