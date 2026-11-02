package org.smrp.controllers.redis.models;


public class Increment {
    
    private String key;
    private long value;

    public Increment(String key, long value) {
        this.key = key;
        this.value = value;
    }
    
    public Increment() {}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
