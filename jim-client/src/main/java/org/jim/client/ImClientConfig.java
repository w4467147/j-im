package org.jim.client;

import org.jim.core.ImHandler;
import org.jim.core.config.ImConfig;
import org.jim.core.listener.ImListener;

/**
 * @ClassName ImClientConfig
 * @Description TODO
 * @Author WChao
 * @Date 2020/1/4 10:42
 * @Version 1.0
 **/
public class ImClientConfig extends ImConfig {

    @Override
    public ImHandler getImHandler() {
        return null;
    }

    @Override
    public ImListener getImListener() {
        return null;
    }
}
