package org.jim.common;

import org.jim.common.packets.Client;
import org.tio.monitor.RateLimiterWrap;

/**
 * 
 * @author wchao 
 *
 */
public class ImSessionContext {
	/**
	 * 消息请求频率控制器
	 */
	protected RateLimiterWrap requestRateLimiter = null;
	
	protected Client client = null;
	
	protected String token = null;

	protected ImChannelContext imChannelContext;

	protected String id;

	/**
	 * @author: WChao
	 * 2017年2月21日 上午10:27:54
	 */
	public ImSessionContext(){}

	public ImSessionContext(ImChannelContext imChannelContext){
		this.imChannelContext = imChannelContext;
	}
	/**
	 * @return the client
	 */
	public Client getClient()
	{
		return client;
	}

	/**
	 * @param client the client to set
	 */
	public void setClient(Client client)
	{
		this.client = client;
	}

	/**
	 * @return the token
	 */
	public String getToken()
	{
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token)
	{
		this.token = token;
	}

	/**
	 * @return the requestRateLimiter
	 */
	public RateLimiterWrap getRequestRateLimiter() {
		return requestRateLimiter;
	}

	/**
	 * @param requestRateLimiter the requestRateLimiter to set
	 */
	public void setRequestRateLimiter(RateLimiterWrap requestRateLimiter) {
		this.requestRateLimiter = requestRateLimiter;
	}

	public ImChannelContext getImChannelContext() {
		return imChannelContext;
	}

	public void setImChannelContext(ImChannelContext imChannelContext) {
		this.imChannelContext = imChannelContext;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
