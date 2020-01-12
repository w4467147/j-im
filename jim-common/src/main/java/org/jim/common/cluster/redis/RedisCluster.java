/**
 * 
 */
package org.jim.common.cluster.redis;

import org.jim.common.ImPacket;
import org.jim.common.cluster.ImCluster;
import org.jim.common.cluster.ImClusterVo;
/**
 * @author WChao
 *
 */
public class RedisCluster extends ImCluster{

	public RedisCluster(RedisClusterConfig clusterConfig) {
		super(clusterConfig);
	}

	@Override
	public void clusterToUser( String userid,ImPacket packet) {
		if (clusterConfig.isCluster4user()) {
			ImClusterVo imClusterVo = new ImClusterVo(packet);
			imClusterVo.setUserid(userid);
			clusterConfig.sendAsyn(imClusterVo);
		}
	}

	@Override
	public void clusterToGroup(String group,ImPacket packet) {
		if(clusterConfig.isCluster4group()){
			ImClusterVo imClusterVo = new ImClusterVo(packet);
			imClusterVo.setGroup(group);
			clusterConfig.sendAsyn(imClusterVo);
		}
	}

	@Override
	public void clusterToIp(String ip,ImPacket packet) {
		if(clusterConfig.isCluster4ip()){
			ImClusterVo imClusterVo = new ImClusterVo(packet);
			imClusterVo.setIp(ip);
			clusterConfig.sendAsyn(imClusterVo);
		}
	}

	@Override
	public void clusterToChannelId(String channelId,ImPacket packet) {
		if(clusterConfig.isCluster4channelId()){
			ImClusterVo imClusterVo = new ImClusterVo(packet);
			imClusterVo.setChannelId(channelId);
			clusterConfig.sendAsyn(imClusterVo);
		}
	}

}
