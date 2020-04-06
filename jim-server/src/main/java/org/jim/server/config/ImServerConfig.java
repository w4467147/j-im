package org.jim.server.config;

import org.jim.common.ImHandler;
import org.jim.common.config.ImConfig;
import org.jim.common.cluster.ImCluster;
import org.jim.common.http.HttpConfig;
import org.jim.common.listener.ImListener;
import org.jim.common.message.MessageHelper;
import org.jim.common.ws.WsConfig;
import org.jim.server.handler.DefaultImServerHandler;
import org.jim.server.handler.ImServerHandler;
import org.jim.server.handler.ImServerHandlerAdapter;
import org.jim.server.listener.DefaultImServerListener;
import org.jim.server.listener.ImServerListener;
import org.jim.server.listener.ImServerListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ssl.SslConfig;
import org.tio.server.ServerTioConfig;

import java.util.Objects;

/**
 * @ClassName ImServerConfig
 * @Description TODO
 * @Author WChao
 * @Date 2020/1/4 10:40
 * @Version 1.0
 **/
public class ImServerConfig extends ImConfig {

    private static Logger log = LoggerFactory.getLogger(ImServerConfig.class);
    /**
     * 服务端消息处理器
     */
    private ImServerHandler imServerHandler;
    /**
     * 服务端消息监听器
     */
    private ImServerListener imServerListener;

    /**
     * 用户消息持久化助手;
     */
    private MessageHelper messageHelper;
    /**
     * 是否开启持久化;
     */
    private String isStore = OFF;
    /**
     * 是否开启集群;
     */
    private String isCluster = OFF;
    /**
     * 是否开启SSL加密
     */
    private String isSSL = OFF;
    /**
     * SSL配置
     */
    private SslConfig sslConfig;
    /**
     * 集群配置
     * 如果此值不为null，就表示要集群
     */
    private ImCluster cluster;

    /**
     * http相关配置;
     */
    private HttpConfig httpConfig;
    /**
     * WebSocket相关配置;
     */
    private WsConfig wsConfig;

    private ImServerConfig(ImServerHandler imServerHandler, ImServerListener imServerListener){
        setImServerHandler(imServerHandler);
        setImServerListener(imServerListener);
        this.tioConfig  = new ServerTioConfig(this.getName(), new ImServerHandlerAdapter(this.imServerHandler),new ImServerListenerAdapter(this.imServerListener));
    }

    public static Builder newBuilder(){
        return new ImServerConfig.Builder();
    }

    @Override
    public ImHandler getImHandler() {
        return getImServerHandler();
    }

    @Override
    public ImListener getImListener() {
        return getImServerListener();
    }

    public static class Builder extends ImConfig.Builder<ImServerConfig,ImServerConfig.Builder>{

        private ImServerListener imServerListener;

        private MessageHelper messageHelper;

        private String isStore = OFF;

        private String isCluster = OFF;

        private String isSSL =  OFF;

        private SslConfig sslConfig;

        private ImCluster cluster;

        private HttpConfig httpConfig;

        private WsConfig wsConfig;

        @Override
        protected Builder getThis() {
            return this;
        }

        public Builder serverListener(ImServerListener imServerListener){
            this.imServerListener = imServerListener;
            return getThis();
        }

        public Builder messageHelper(MessageHelper messageHelper){
            this.messageHelper = messageHelper;
            return getThis();
        }

        public Builder isStore(String isStore){
            this.isStore = isStore;
            return getThis();
        }

        public Builder isCluster(String isCluster){
            this.isCluster = isCluster;
            return getThis();
        }

        public Builder isSSL(String isSSL){
            this.isSSL = isSSL;
            return getThis();
        }

        public Builder sslConfig(SslConfig sslConfig){
            this.sslConfig = sslConfig;
            return getThis();
        }

        public Builder cluster(ImCluster cluster){
            this.cluster = cluster;
            return getThis();
        }

        public Builder httConfig(HttpConfig httpConfig){
            this.httpConfig = httpConfig;
            return getThis();
        }

        public Builder wsConfig(WsConfig wsConfig){
            this.wsConfig = wsConfig;
            return getThis();
        }

        @Override
        public ImServerConfig build(){
            ImServerConfig imServerConfig = new ImServerConfig(new DefaultImServerHandler(), this.imServerListener);
            imServerConfig.setBindIp(this.bindIp);
            imServerConfig.setBindPort(this.bindPort);
            imServerConfig.setReadBufferSize(this.readBufferSize);
            imServerConfig.setMessageHelper(this.messageHelper);
            imServerConfig.setIsStore(this.isStore);
            imServerConfig.setIsCluster(this.isCluster);
            imServerConfig.setIsSSL(this.isSSL);
            imServerConfig.setSslConfig(this.sslConfig);
            imServerConfig.setCluster(this.cluster);
            imServerConfig.setHttpConfig(this.httpConfig);
            imServerConfig.setWsConfig(this.wsConfig);
            imServerConfig.setHeartbeatTimeout(this.heartbeatTimeout);
            imServerConfig.setImGroupListener(this.imGroupListener);
            imServerConfig.setImUserListener(this.imUserListener);
            return imServerConfig;
        }
    }

    public ImServerListener getImServerListener() {
        return imServerListener;
    }

    public void setImServerHandler(ImServerHandler imServerHandler) {
        this.imServerHandler = imServerHandler;
        if(Objects.isNull(this.imServerHandler)){
            this.imServerHandler = new DefaultImServerHandler();
        }
    }

    public void setImServerListener(ImServerListener imServerListener) {
        this.imServerListener = imServerListener;
        if(Objects.isNull(this.imServerListener)){
            this.imServerListener = new DefaultImServerListener();
        }
    }

    public MessageHelper getMessageHelper() {
        return messageHelper;
    }

    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    public String getIsStore() {
        return isStore;
    }

    public void setIsStore(String isStore) {
        this.isStore = isStore;
    }

    public String getIsCluster() {
        return isCluster;
    }

    /**
     * 设置是否开启集群
     * @param isCluster
     */
    public void setIsCluster(String isCluster) {
        this.isCluster = isCluster;
    }

    public String getIsSSL() {
        return isSSL;
    }

    public void setIsSSL(String isSSL) {
        this.isSSL = isSSL;
    }

    public SslConfig getSslConfig() {
        return sslConfig;
    }

    public void setSslConfig(SslConfig sslConfig) {
        this.sslConfig = sslConfig;
        this.tioConfig.setSslConfig(sslConfig);
    }

    public ImCluster getCluster() {
        return cluster;
    }

    public void setCluster(ImCluster cluster) {
        this.cluster = cluster;

    }

    public HttpConfig getHttpConfig() {
        return httpConfig;
    }

    public void setHttpConfig(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
    }

    public WsConfig getWsConfig() {
        return wsConfig;
    }

    public void setWsConfig(WsConfig wsConfig) {
        this.wsConfig = wsConfig;
    }

    public ImServerHandler getImServerHandler() {
        return imServerHandler;
    }
}
