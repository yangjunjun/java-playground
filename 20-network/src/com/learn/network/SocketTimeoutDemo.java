package com.learn.network;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * 超时演示：连接超时（connect timeout）与读超时（SO_TIMEOUT）。
 * 仅使用 127.0.0.1 回环，不访问外网。
 */
public class SocketTimeoutDemo {

    public static void demo() {
        // ---- 读超时：服务端接受连接但故意不发数据 ----
        try (ServerSocket server = new ServerSocket(0, 8, InetAddress.getByName("127.0.0.1"))) {
            int port = server.getLocalPort();

            Thread silent = new Thread(() -> {
                try (Socket conn = server.accept()) {
                    Thread.sleep(300); // 故意不回数据，逼客户端读超时
                } catch (Exception ignored) {
                }
            }, "silent-server");
            silent.start();

            try (Socket client = new Socket()) {
                client.connect(new InetSocketAddress("127.0.0.1", port), 1000);
                client.setSoTimeout(150); // 读超时 150ms，故意小于服务端的 300ms
                InputStream in = client.getInputStream();
                try {
                    int b = in.read();
                    System.out.println("意外读到: " + b);
                } catch (SocketTimeoutException e) {
                    System.out.println("读超时（预期行为）: " + e.getClass().getSimpleName()
                            + " —— SO_TIMEOUT 生效，线程不会永久卡住");
                }
            }
            silent.join(1000);
        } catch (Exception e) {
            System.out.println("读超时演示异常: " + e);
        }

        // ---- 连接失败 / 连接超时 ----
        // 先取一个空闲端口并立刻关闭，再连它 → 通常快速“连接被拒绝”
        int freePort;
        try (ServerSocket probe = new ServerSocket(0, 1, InetAddress.getByName("127.0.0.1"))) {
            freePort = probe.getLocalPort();
        } catch (Exception e) {
            System.out.println("分配端口异常: " + e);
            return;
        }

        try (Socket client = new Socket()) {
            client.connect(new InetSocketAddress("127.0.0.1", freePort), 300);
            System.out.println("意外连接成功");
        } catch (Exception e) {
            System.out.println("连已关闭的端口: " + e.getClass().getSimpleName()
                    + "（连接被拒绝/超时，都应在有限时间内返回）");
        }

        System.out.println("结论：必须设置 connect timeout 与 read timeout，否则线程可能被网络 I/O 永久挂起");
    }
}
