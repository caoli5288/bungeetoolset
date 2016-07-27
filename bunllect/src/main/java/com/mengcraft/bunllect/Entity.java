package com.mengcraft.bunllect;

/**
 * Created on 16-7-27.
 */
public class Entity {
    private String name;
    private String ip;
    private int life;
    private String instance;
    private String host;

    public Entity(String name, String ip, int life, String instance, String host) {
        this.name = name;
        this.ip = ip;
        this.life = life;
        this.instance = instance;
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean valid() {
        return life != 0;
    }
}
