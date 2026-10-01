package com.learn.greet;

/**
 * greet 模块提供的问候服务。
 * 只有被 module-info.java exports 的包才能被其他模块使用。
 */
public class Greeter {

    public String greet(String name) {
        return "你好, " + name + "！（来自 greet 模块）";
    }

    public String version() {
        return "greet/1.0";
    }
}
