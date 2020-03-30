package org.jim.server.command.handler.processor.group;

import org.jim.common.ImChannelContext;
import org.jim.common.packets.Group;
import org.jim.common.packets.JoinGroupRespBody;
import org.jim.server.command.handler.processor.SingleProtocolCmdProcessor;
/**
 * @author ensheng
 */
public interface GroupCmdProcessor extends SingleProtocolCmdProcessor {
    /**
     * 加入群组处理
     * @param joinGroup
     * @param imChannelContext
     * @return
     */
    JoinGroupRespBody join(Group joinGroup, ImChannelContext imChannelContext);
}
