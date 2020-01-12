package org.jim.server.util;
import org.jim.common.ImChannelContext;
import org.jim.common.ImSessionContext;
import org.jim.server.config.ImServerConfig;
import org.tio.core.ChannelContext;
import org.jim.common.http.HttpConfig;
import org.jim.common.http.HttpRequest;
/**
 * @author WChao
 * 2017年8月18日 下午5:47:00
 */
public class HttpServerUtils {
	/**
	 *
	 * @param request
	 * @return
	 * @author WChao
	 */
	public static HttpConfig getHttpConfig(HttpRequest request) {
		ImServerConfig imServerConfig = (ImServerConfig)request.getImChannelContext().getImConfig();
		return imServerConfig.getHttpConfig();
	}

	/**
	 * @param args
	 * @author WChao
	 */
	public static void main(String[] args) {

	}

	/**
	 *
	 * @author WChao
	 */
	public HttpServerUtils() {
	}
}
