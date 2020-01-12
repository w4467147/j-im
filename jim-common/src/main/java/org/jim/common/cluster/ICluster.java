/**
 * 
 */
package org.jim.common.cluster;

import org.jim.common.ImPacket;

/**
 * 
 * @author WChao
 *
 */
public interface ICluster {
	public void clusterToUser(String userId,ImPacket packet);
	public void clusterToGroup(String group,ImPacket packet);
	public void clusterToIp(String ip,ImPacket packet);
	public void clusterToChannelId(String channelId,ImPacket packet);
}
