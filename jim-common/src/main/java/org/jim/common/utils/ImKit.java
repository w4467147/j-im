/**
 * 
 */
package org.jim.common.utils;

import cn.hutool.core.bean.BeanUtil;
import org.jim.common.ImChannelContext;
import org.jim.common.ImConst;
import org.jim.common.ImPacket;
import org.jim.common.ImStatus;
import org.jim.common.exception.ImException;
import org.jim.common.http.HttpProtocol;
import org.jim.common.packets.Command;
import org.jim.common.packets.Group;
import org.jim.common.packets.RespBody;
import org.jim.common.packets.User;
import org.jim.common.protocol.AbstractProtocol;
import org.jim.common.protocol.IProtocolConverter;
import org.jim.common.protocol.IProtocol;
import org.jim.common.tcp.TcpProtocol;
import org.jim.common.ws.WsProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
 * IM工具类;
 * @author WChao
 *
 */
public class ImKit {
	
	private static Logger logger = LoggerFactory.getLogger(ImKit.class);


	/**
	 * 格式化状态码消息响应体;
	 * @param status
	 * @return
	 */
	public static byte[] toImStatusBody(ImStatus status){
		return JsonKit.toJsonBytes(new RespBody(status).setMsg(status.getDescription()+" "+status.getText()));
	}

	/**
     * 复制用户信息不包括friends、groups下的users信息;
     * @param source
     * @return
     */
    public static User copyUserWithoutFriendsGroups(User source){
		 if(source == null) {
			 return null;
		 }
		 User user = new User();
		 BeanUtil.copyProperties(source, user,"friends","groups");
		 return user;
    }

    /**
     * 复制用户信息不包括friends、groups下的users信息;
     * @param source
     * @return
     */
    public static User copyUserWithoutUsers(User source){
		 if(source == null){
			 return source;
		 }
		 User user = new User();
		 BeanUtil.copyProperties(source, user,"friends","groups");
		 List<Group> friends = source.getFriends();
		 if(friends != null && !friends.isEmpty()){
			 List<Group> newFriends = new ArrayList<Group>();
			 for(Group friend : friends){
				 Group newFriend = new Group();
				 BeanUtil.copyProperties(friend, newFriend);
				 newFriend.setUsers(null);
				 newFriends.add(newFriend);
			 }
			 user.setFriends(newFriends);
		 }
		 List<Group> groups = source.getGroups();
		 if(groups != null && !groups.isEmpty()){
			 List<Group> newGroups = new ArrayList<Group>();
			 for(Group group : groups){
				 Group newGroup = new Group();
				 BeanUtil.copyProperties(group, newGroup);
				 newGroup.setUsers(null);
				 newGroups.add(newGroup);
			 }
			 user.setGroups(newGroups);
		 }
		 return user;
    }

    /**
     * 复制分组或者群组，不包括users;
     * @param source
     * @return
     */
    public static Group copyGroupWithoutUsers(Group source){
		 if(source == null) {
			 return null;
		 }
		 Group group = new Group();
		 BeanUtil.copyProperties(source, group,"users");
	 	 return group;
	}
}
