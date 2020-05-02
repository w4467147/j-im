package org.jim.core.ws;

import org.tio.core.intf.TioUuid;
import org.tio.utils.hutool.Snowflake;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author WChao
 * 2017年6月5日 上午10:44:26
 */
public class WsTioUuid implements TioUuid {
	private Snowflake snowflake;

	public WsTioUuid() {
		snowflake = new Snowflake(ThreadLocalRandom.current().nextInt(1, 30), ThreadLocalRandom.current().nextInt(1, 30));
	}

	public WsTioUuid(long workerId, long dataCenterId) {
		snowflake = new Snowflake(workerId, dataCenterId);
	}

	/**
	 * @return
	 * @author wchao
	 */
	@Override
	public String uuid() {
		return snowflake.nextId() + "";
	}
}
