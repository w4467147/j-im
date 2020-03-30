/**
 * 
 */
package org.jim.server.http;

import org.jim.common.ImChannelContext;
import org.jim.common.ImConst;
import org.jim.common.ImPacket;
import org.jim.common.Jim;
import org.jim.common.config.ImConfig;
import org.jim.common.exception.ImDecodeException;
import org.jim.common.exception.ImException;
import org.jim.common.http.*;
import org.jim.common.http.handler.IHttpRequestHandler;
import org.jim.common.protocol.AbstractProtocol;
import org.jim.common.session.id.impl.UUIDSessionIdGenerator;
import org.jim.server.ImServerStarter;
import org.jim.server.config.ImServerConfig;
import org.jim.server.handler.AbstractProtocolHandler;
import org.jim.server.http.mvc.Routes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.SystemTimer;
import org.tio.utils.cache.guava.GuavaCache;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * 版本: [1.0]
 * 功能说明: 
 * @author : WChao 创建时间: 2017年8月3日 下午3:07:54
 */
public class HttpProtocolHandler extends AbstractProtocolHandler {
	
	private Logger log = LoggerFactory.getLogger(HttpProtocolHandler.class);

	private HttpConfig httpConfig;
	
	private IHttpRequestHandler httpRequestHandler;

	public HttpProtocolHandler(){
		this(null, new HttpProtocol(new HttpConvertPacket()));
	};

	public HttpProtocolHandler(HttpConfig httpConfig, AbstractProtocol protocol){
		super(protocol);
		this.httpConfig = httpConfig;
	}

	@Override
	public void init(ImServerConfig imConfig)throws ImException {
		long start = SystemTimer.currentTimeMillis();
		this.httpConfig = imConfig.getHttpConfig();
		if (Objects.isNull(httpConfig.getSessionStore())) {
			GuavaCache guavaCache = GuavaCache.register(httpConfig.getSessionCacheName(), null, httpConfig.getSessionTimeout());
			httpConfig.setSessionStore(guavaCache);
		}
		if (Objects.isNull(httpConfig.getSessionIdGenerator())) {
			httpConfig.setSessionIdGenerator(UUIDSessionIdGenerator.instance);
		}
		if(Objects.isNull(httpConfig.getScanPackages())){
			//J-IM MVC需要扫描的根目录包
			String[] scanPackages = new String[] { ImServerStarter.class.getPackage().getName() };
			httpConfig.setScanPackages(scanPackages);
		}else{
			String[] scanPackages = new String[httpConfig.getScanPackages().length+1];
			scanPackages[0] = ImServerStarter.class.getPackage().getName();
			System.arraycopy(httpConfig.getScanPackages(), 0, scanPackages, 1, httpConfig.getScanPackages().length);
			httpConfig.setScanPackages(scanPackages);
		}
		Routes routes = new Routes(httpConfig.getScanPackages());
		httpRequestHandler = new DefaultHttpRequestHandler(httpConfig, routes);
		httpConfig.setHttpRequestHandler(httpRequestHandler);
		long end = SystemTimer.currentTimeMillis();
		long iv = end - start;
		log.info("J-IM Http协议初始化完毕,耗时:{}ms", iv);
	}
	
	@Override
	public ByteBuffer encode(ImPacket imPacket, ImConfig imConfig, ImChannelContext imChannelContext) {
		HttpResponse httpResponsePacket = (HttpResponse) imPacket;
		ByteBuffer byteBuffer = HttpResponseEncoder.encode(httpResponsePacket, imChannelContext,false);
		return byteBuffer;
	}

	@Override
	public void handler(ImPacket imPacket, ImChannelContext imChannelContext)throws ImException {
		HttpRequest httpRequestPacket = (HttpRequest) imPacket;
		HttpResponse httpResponsePacket = httpRequestHandler.handler(httpRequestPacket, httpRequestPacket.getRequestLine());
		Jim.send(imChannelContext, httpResponsePacket);
	}

	@Override
	public ImPacket decode(ByteBuffer buffer, int limit, int position, int readableLength, ImChannelContext imChannelContext)throws ImDecodeException {
		HttpRequest request = HttpRequestDecoder.decode(buffer, imChannelContext,true);
		imChannelContext.setAttribute(ImConst.HTTP_REQUEST,request);
		return request;
	}
	
	public IHttpRequestHandler getHttpRequestHandler() {
		return httpRequestHandler;
	}

	public void setHttpRequestHandler(IHttpRequestHandler httpRequestHandler) {
		this.httpRequestHandler = httpRequestHandler;
	}
	
	public HttpConfig getHttpConfig() {
		return httpConfig;
	}

	public void setHttpConfig(HttpConfig httpConfig) {
		this.httpConfig = httpConfig;
	}

}
