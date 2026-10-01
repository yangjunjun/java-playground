package com.learn.network;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

/**
 * 网络工具与常识：端口范围、回环地址、本地端口探测、常见注意点。
 */
public class NetworkTools {

    public static void demo() {
        // ---- 端口常识 ----
        // 0–1023 周知端口（HTTP 80、HTTPS 443）；1024–49151 注册端口；49152–65535 动态/私有端口
        System.out.println("端口范围: 0–1023 周知端口 / 1024–49151 注册 / 49152–65535 动态");
        System.out.println("绑定端口 0 = 让系统分配一个当前空闲的动态端口");

        // ---- 回环探测：判断本机某端口是否已有服务（不连外部网）----
        int[] candidates = { 1, 7, 8080 }; // 1/7 通常无服务，8080 大概率也无
        for (int port : candidates) {
            System.out.println("127.0.0.1:" + port + " 端口开放吗? " + isLocalPortOpen(port));
        }

        // ---- 本机可用端口分配 ----
        try (ServerSocket probe = new ServerSocket(0, 1, InetAddress.getByName("127.0.0.1"))) {
            System.out.println("系统分配的临时端口示例: " + probe.getLocalPort());
        } catch (Exception e) {
            System.out.println("端口探测异常: " + e);
        }

        // ---- InetSocketAddress 常用构造 ----
        InetSocketAddress unresolved = InetSocketAddress.createUnresolved("example.com", 443);
        System.out.println("createUnresolved 不做 DNS 解析: " + unresolved);
        System.out.println("（本示例不真正解析外网域名，只展示 API 形态）");

        // ---- 真实项目注意点 ----
        System.out.println("真实网络编程必做：设置 connect/read 超时、关闭资源、处理 TLS、限制响应体大小");
    }

    /** 尝试对 127.0.0.1:port 建立 TCP 连接，成功说明端口有服务在听。 */
    private static boolean isLocalPortOpen(int port) {
        try (java.net.Socket socket = new java.net.Socket()) {
            socket.connect(new InetSocketAddress("127.0.0.1", port), 150);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
