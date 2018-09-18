package com.choice.orderupload.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 林金成
 * @date 2018/9/14 14:17
 */
@Component
@ConfigurationProperties
public class ApplicationParameter {
    @Value("${log.dir}")
    private String logDir;
    @Value("${file.dir}")
    private String fileDir;
    @Value("${next.host}")
    private String nextHost;
    @Value("${next.port}")
    private Integer nextPort;
    @Value("${choice.gateway}")
    private String choiceGateway;
    @Value("${oauth.appid}")
    private String oauthAppId;
    @Value("${oauth.secret}")
    private String oauthSecret;

    public String getLogDir() {
        return logDir;
    }

    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public String getNextHost() {
        return nextHost;
    }

    public void setNextHost(String nextHost) {
        this.nextHost = nextHost;
    }

    public Integer getNextPort() {
        return nextPort;
    }

    public void setNextPort(Integer nextPort) {
        this.nextPort = nextPort;
    }

    public String getChoiceGateway() {
        return choiceGateway;
    }

    public void setChoiceGateway(String choiceGateway) {
        this.choiceGateway = choiceGateway;
    }

    public String getOauthAppId() {
        return oauthAppId;
    }

    public void setOauthAppId(String oauthAppId) {
        this.oauthAppId = oauthAppId;
    }

    public String getOauthSecret() {
        return oauthSecret;
    }

    public void setOauthSecret(String oauthSecret) {
        this.oauthSecret = oauthSecret;
    }
}