package org.jim.common.listener;

import org.jim.common.ImChannelContext;
import org.jim.common.exception.ImException;

/**
 * @ClassName ImUserListener
 * @Description TODO
 * @Author WChao
 * @Date 2020/1/12 14:24
 * @Version 1.0
 **/
public interface ImUserListener {
    /**
     * 绑定用户后回调该方法
     * @param imChannelContext
     * @param userId
     * @throws Exception
     * @author WChao
     */
    void onAfterBind(ImChannelContext imChannelContext, String userId) throws ImException;

    /**
     * 解绑用户后回调该方法
     * @param imChannelContext
     * @param userId
     * @throws Exception
     * @author WChao
     */
    void onAfterUnbind(ImChannelContext imChannelContext, String userId) throws ImException;
}
