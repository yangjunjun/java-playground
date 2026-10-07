package com.learn.network;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * 地址与 URL/URI 基础：InetAddress、InetSocketAddress、URI/URL。
 * 全部只访问 localhost，不依赖外部网络。
 */
public class UrlBasics {

    public static void demo() {
        try {
            // ---- InetAddress：IP 地址对象（这里只查本机）----
            InetAddress local = InetAddress.getLocalHost();
            System.out.println("本机主机名: " + local.getHostName());
            System.out.println("本机 IP: " + local.getHostAddress());

            InetAddress loopback = InetAddress.getByName("localhost");
            System.out.println("localhost: " + loopback.getHostAddress()
                    + ", 是否回环: " + loopback.isLoopbackAddress());

            // getAllByName：一个主机名可能对应多个 IP（IPv4/IPv6）
            InetAddress[] all = InetAddress.getAllByName("localhost");
            System.out.println("localhost 解析出 " + all.length + " 个地址");
            for (InetAddress a : all) {
                System.out.println("  " + a.getHostAddress());
            }

            // ---- InetSocketAddress：IP + 端口 ----
            InetSocketAddress addr = new InetSocketAddress("127.0.0.1", 8080);
            System.out.println("InetSocketAddress: " + addr.getHostString() + ":" + addr.getPort());
            System.out.println("端口 0 表示让系统分配临时端口（ephemeral port）");

            // ---- URI 与 URL ----
            URI uri = new URI("https://example.com:8443/docs/java?lang=zh#intro");
            System.out.println("URI scheme = " + uri.getScheme()
                    + ", host = " + uri.getHost()
                    + ", port = " + uri.getPort()
                    + ", path = " + uri.getPath()
                    + ", query = " + uri.getQuery()
                    + ", fragment = " + uri.getFragment());

            // URL 是 URI 的“可打开连接”子集；URI 更通用、不可变
            URL url = uri.toURL();
            System.out.println("URL 协议 = " + url.getProtocol() + ", 端口 = " + url.getPort());
        } catch (UnknownHostException e) {
            System.out.println("主机解析失败: " + e.getMessage());
        } catch (URISyntaxException e) {
            System.out.println("URI 语法错误: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("演示异常: " + e);
        }
    }
}
