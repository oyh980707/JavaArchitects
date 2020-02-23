package com.loveoyh.PrototypePattern.DeepClone;

import java.io.Serializable;

/**
 * 身份类
 */
public class Identity implements Serializable {
    private String id;
    private String name;

    public Identity() {
        this("0001","Dolly");
    }

    public Identity(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
