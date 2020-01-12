package org.jim.common.listener;

import org.jim.common.ImChannelContext;
import org.jim.common.exception.ImException;
import org.tio.core.ChannelContext;

/**
 * @ClassName ImGroupListener
 * @Description TODO
 * @Author WChao
 * @Date 2020/1/12 14:17
 * @Version 1.0
 **/
public interface ImGroupListener {
    /**
     * 绑定群组后回调该方法
     * @param imChannelContext
     * @param group
     * @throws ImException
     * @author WChao
     */
    void onAfterBind(ImChannelContext imChannelContext, String group) throws ImException;

    /**
     * 解绑群组后回调该方法
     * @param imChannelContext
     * @param group
     * @throws ImException
     * @author WChao
     */
    void onAfterUnbind(ImChannelContext imChannelContext, String group) throws ImException;
}
