package com.learn.network;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP 回声（Echo）演示：ServerSocket + Socket，仅在 127.0.0.1 回环上收发字节。
 */
public class TcpEcho {

    public static void demo() {
        try {
            // 服务端：绑定 127.0.0.1 的临时端口（端口 0）
            try (ServerSocket server = new ServerSocket(0, 8, InetAddress.getByName("127.0.0.1"))) {
                int port = server.getLocalPort();
                System.out.println("Echo 服务端已在 127.0.0.1:" + port + " 监听");

                // 起一个线程处理一次连接：读到什么就原样写回
                Thread serverThread = new Thread(() -> {
                    try (Socket conn = server.accept();
                         InputStream in = conn.getInputStream();
                         OutputStream out = conn.getOutputStream()) {
                        byte[] buf = new byte[1024];
                        int n = in.read(buf); // 阻塞等待客户端数据
                        if (n > 0) {
                            out.write(buf, 0, n);
                            out.flush();
                        }
                    } catch (Exception e) {
                        System.out.println("服务端异常: " + e);
                    }
                }, "tcp-echo-server");
                serverThread.start();

                // 客户端：连接 → 发送 → 读回 → 关闭
                try (Socket client = new Socket()) {
                    client.connect(new InetSocketAddress("127.0.0.1", port), 2000);
                    client.setSoTimeout(2000); // 读超时 2 秒
                    OutputStream out = client.getOutputStream();
                    InputStream in = client.getInputStream();

                    byte[] payload = "你好，TCP Echo！".getBytes("UTF-8");
                    out.write(payload);
                    out.flush();
                    System.out.println("客户端已发送 " + payload.length + " 字节");

                    byte[] buf = new byte[1024];
                    int n = in.read(buf);
                    String echoed = new String(buf, 0, n, "UTF-8");
                    System.out.println("客户端读回: " + echoed);
                }

                serverThread.join(3000);
            }
        } catch (Exception e) {
            System.out.println("TCP Echo 演示异常: " + e);
        }
    }
}
