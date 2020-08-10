package dev.imabad.mceventsuite.spigot;

import dev.imabad.mceventsuite.core.modules.redis.RedisBaseMessage;

public class TestRedisMessage extends RedisBaseMessage {

    private String hello = "world";

    public String getHello() {
        return hello;
    }
}
