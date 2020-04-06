/**
 * 
 */
package org.jim.server;

import org.jim.common.ImConst;
import org.jim.common.cache.redis.RedissonTemplate;
import org.jim.common.cluster.redis.RedisCluster;
import org.jim.common.cluster.redis.RedisClusterConfig;
import org.jim.common.config.ImConfig;
import org.jim.server.config.ImServerConfig;
import org.jim.server.handler.ProtocolManager;
import org.jim.server.helper.redis.RedisMessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.server.ServerTioConfig;
import org.tio.server.TioServer;
import java.io.IOException;

/**
 * J-IM服务端启动类
 * @author WChao
 *
 */
public class ImServerStarter {

	private static Logger log = LoggerFactory.getLogger(ImServerStarter.class);
	private TioServer tioServer = null;
	private ImServerConfig imServerConfig;

	public ImServerStarter(ImServerConfig imServerConfig){
		this.imServerConfig = imServerConfig;
		init(imServerConfig);
	}
	
	public void init(ImServerConfig imServerConfig){
		ImConfig.Global.set(imServerConfig);
		System.setProperty("tio.default.read.buffer.size", String.valueOf(imServerConfig.getReadBufferSize()));
		if(imServerConfig.getMessageHelper() == null){
			imServerConfig.setMessageHelper(new RedisMessageHelper());
		}
		if(ImConfig.ON.equals(imServerConfig.getIsCluster())){
			imServerConfig.setIsStore(ImConfig.ON);
			if(imServerConfig.getCluster() == null){
				try{
					imServerConfig.setCluster(new RedisCluster(RedisClusterConfig.newInstance(ImConst.Topic.REDIS_CLUSTER_TOPIC_SUFFIX, RedissonTemplate.me().getRedissonClient())));
				}catch(Exception e){
					log.error("连接集群配置出现异常,请检查！",e);
				}
			}
		}
		ProtocolManager.init();
		tioServer = new TioServer((ServerTioConfig)imServerConfig.getTioConfig());
	}
	
	public void start() throws IOException {
		tioServer.start(this.imServerConfig.getBindIp(), this.imServerConfig.getBindPort());
	}
	
	public void stop(){
		tioServer.stop();
	}
}
