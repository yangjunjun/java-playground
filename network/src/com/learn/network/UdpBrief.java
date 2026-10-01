package com.learn.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * UDP 简明演示：DatagramSocket 收发数据报，无连接、不保证可靠。
 */
public class UdpBrief {

    public static void demo() {
        try {
            // 服务端：绑定临时端口，收到数据报就回显
            try (DatagramSocket server = new DatagramSocket(0, InetAddress.getByName("127.0.0.1"))) {
                int port = server.getLocalPort();
                System.out.println("UDP 服务端已在 127.0.0.1:" + port + " 等待数据报");
                server.setSoTimeout(2000);

                Thread serverThread = new Thread(() -> {
                    try {
                        byte[] buf = new byte[1024];
                        DatagramPacket pkt = new DatagramPacket(buf, buf.length);
                        server.receive(pkt); // 阻塞等待

                        String text = new String(pkt.getData(), 0, pkt.getLength(), "UTF-8");
                        System.out.println("UDP 服务端收到: " + text);

                        byte[] echo = text.getBytes("UTF-8");
                        DatagramPacket resp = new DatagramPacket(
                                echo, echo.length, pkt.getAddress(), pkt.getPort());
                        server.send(resp);
                    } catch (Exception e) {
                        System.out.println("UDP 服务端异常: " + e);
                    }
                }, "udp-echo-server");
                serverThread.start();

                // 客户端：发一个数据报，再收回应
                try (DatagramSocket client = new DatagramSocket()) {
                    client.setSoTimeout(2000);
                    byte[] data = "你好，UDP！".getBytes("UTF-8");
                    DatagramPacket send = new DatagramPacket(
                            data, data.length, InetAddress.getByName("127.0.0.1"), port);
                    client.send(send);
                    System.out.println("UDP 客户端已发送数据报");

                    byte[] buf = new byte[1024];
                    DatagramPacket recv = new DatagramPacket(buf, buf.length);
                    client.receive(recv);
                    System.out.println("UDP 客户端收到回应: "
                            + new String(recv.getData(), 0, recv.getLength(), "UTF-8"));
                }

                serverThread.join(3000);
            }
        } catch (Exception e) {
            System.out.println("UDP 演示异常: " + e);
        }
    }
}
