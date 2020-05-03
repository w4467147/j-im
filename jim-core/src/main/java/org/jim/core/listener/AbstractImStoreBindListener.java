/**
 * 
 */
package org.jim.core.listener;

import org.jim.core.ImConst;
import org.jim.core.config.ImConfig;

/**
 * @author WChao
 * 2018/08/26
 */
public abstract class AbstractImStoreBindListener implements ImStoreBindListener, ImConst {
	
	protected ImConfig imConfig;

	public ImConfig getImConfig() {
		return imConfig;
	}

	public void setImConfig(ImConfig imConfig) {
		this.imConfig = imConfig;
	}
}
