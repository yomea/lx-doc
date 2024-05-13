package com.lxqnsys.doc.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "public.redis.sentinel")
public class RedissonProperty {
    private String password;
    private String master;
    private String node;
    private int timeout = 5000;
    private int masterSize = 10;
    private int slaveSize = 10;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }


    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getMasterSize() {
        return masterSize;
    }

    public void setMasterSize(int masterSize) {
        this.masterSize = masterSize;
    }

    public int getSlaveSize() {
        return slaveSize;
    }

    public void setSlaveSize(int slaveSize) {
        this.slaveSize = slaveSize;
    }
}