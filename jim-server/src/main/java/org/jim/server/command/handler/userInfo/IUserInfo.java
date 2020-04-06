package org.jim.server.command.handler.userInfo;

import org.jim.common.ImChannelContext;
import org.jim.common.packets.User;
import org.jim.common.packets.UserReqBody;

public interface IUserInfo {
    /**
     * 获取用户信息接口
     * @param userReqBody
     * @param imChannelContext
     * @return
     */
    User getUserInfo (UserReqBody userReqBody, ImChannelContext imChannelContext);
}
