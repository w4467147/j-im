package org.jim.common.listener;

import org.jim.common.ImChannelContext;
import org.jim.common.ImConst;
import org.tio.core.ChannelContext;
import org.tio.core.intf.GroupListener;

/**
 * @ClassName ImGroupListenerAdapter
 * @Description TODO
 * @Author WChao
 * @Date 2020/1/12 14:19
 * @Version 1.0
 **/
public class ImGroupListenerAdapter implements GroupListener, ImConst {

    private ImGroupListener imGroupListener;

    public ImGroupListenerAdapter(ImGroupListener imGroupListener){
        this.imGroupListener = imGroupListener;
    }

    @Override
    public void onAfterBind(ChannelContext channelContext, String group) throws Exception {
        ImChannelContext imChannelContext = (ImChannelContext)channelContext.get(Key.IM_CHANNEL_CONTEXT_KEY);
        imGroupListener.onAfterBind(imChannelContext, group);
    }

    @Override
    public void onAfterUnbind(ChannelContext channelContext, String group) throws Exception {
        ImChannelContext imChannelContext = (ImChannelContext)channelContext.get(Key.IM_CHANNEL_CONTEXT_KEY);
        imGroupListener.onAfterUnbind(imChannelContext, group);
    }
}
