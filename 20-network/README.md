# 网络编程（Network）

Java 通过 `java.net` 提供 TCP/UDP 网络能力，Java 11 起 `java.net.http` 提供现代 HTTP 客户端。本模块**全部只访问 127.0.0.1 回环地址**，不依赖外部网络：TCP/UDP 回声、本地 HTTP 服务均在进程内自建。

## 1. InetAddress 与 InetSocketAddress

`InetAddress` 表示 IP 地址；`InetSocketAddress` = IP + 端口：

```java
InetAddress local = InetAddress.getLocalHost();
InetAddress loopback = InetAddress.getByName("localhost"); // 回环地址

InetSocketAddress addr = new InetSocketAddress("127.0.0.1", 8080);
// 端口 0 = 让系统分配临时端口
```

## 2. URI 与 URL

`URI` 是通用资源标识符（不可变、可解析）；`URL` 是其中“能打开连接”的子集：

```java
URI uri = new URI("https://example.com:8443/docs/java?lang=zh#intro");
uri.getScheme();   // https
uri.getHost();     // example.com
uri.getPath();     // /docs/java
uri.getQuery();    // lang=zh
```

## 3. TCP：Socket / ServerSocket

TCP 是面向连接、可靠的字节流。典型流程：

```java
// 服务端
try (ServerSocket server = new ServerSocket(0, 8, InetAddress.getByName("127.0.0.1"))) {
    Socket conn = server.accept();          // 阻塞等待
    // 读 InputStream / 写 OutputStream ...
}

// 客户端
try (Socket client = new Socket()) {
    client.connect(new InetSocketAddress("127.0.0.1", port), 2000);
    client.setSoTimeout(2000);              // 读超时
    client.getOutputStream().write(data);
}
```

`accept()` 之后每个连接一般交给独立线程或 NIO `Selector` 处理。

## 4. UDP：DatagramSocket

UDP 无连接、不保证可靠、可能丢包乱序，但延迟低：

```java
try (DatagramSocket sock = new DatagramSocket(0, InetAddress.getByName("127.0.0.1"))) {
    sock.send(new DatagramPacket(data, data.length, dest, port));
    sock.receive(pkt);
}
```

## 5. HttpClient（Java 11+）

```java
HttpClient client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(2))
        .build();

HttpRequest req = HttpRequest.newBuilder()
        .uri(URI.create("https://api.example.com/hello"))
        .timeout(Duration.ofSeconds(5))
        .header("Accept", "application/json")
        .GET()
        .build();

HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
```

异步用 `sendAsync`；WebSocket 用 `client.newWebSocketBuilder()`（Java 11+）。
本模块用 JDK 自带 `com.sun.net.httpserver.HttpServer` 在回环上起了个假服务，让 `HttpClient` 真实收发一次。

## 6. 端口与超时

| 概念 | 说明 |
|------|------|
| 周知端口 | 0–1023（80 HTTP、443 HTTPS） |
| 注册端口 | 1024–49151 |
| 动态端口 | 49152–65535；绑定 0 时系统从中分配 |
| connect timeout | `socket.connect(addr, timeout)` / `HttpClient.connectTimeout` |
| read timeout | `socket.setSoTimeout(ms)`；超时抛 `SocketTimeoutException` |

**真实网络代码必须设置超时**，否则线程可能被 I/O 永久挂起。

## 7. 真实项目注意点

- 超时：连接、读、写都要设
- TLS：对外 HTTPS，校验证书，不要 `TrustAllCerts`
- 资源关闭：`Socket` / `InputStream` / `OutputStream` 用 try-with-resources
- 响应体大小限制，防止恶意大响应撑爆内存
- 不要用 `isReachable()` 做健康检查（依赖 ICMP，常被防火墙拦）

## 8. 运行本模块

**VS Code**：打开 `src/com/learn/network/Main.java`，点击 `main` 上方的 **Run**。

**命令行**：

```bash
./run.sh
```

## 练习文件

| 文件 | 内容 |
|------|------|
| `Main.java` | 总入口，依次运行全部示例 |
| `UrlBasics.java` | InetAddress / InetSocketAddress / URI / URL |
| `TcpEcho.java` | TCP Socket/ServerSocket 回声 |
| `UdpBrief.java` | UDP DatagramSocket 数据报 |
| `HttpClientDemo.java` | HttpClient 请求构建与本地收发、WebSocket API 简介 |
| `NetworkTools.java` | 端口范围、回环探测、InetSocketAddress |
| `SocketTimeoutDemo.java` | 读超时 / 连接失败的有限时间返回 |

## 易错点

1. 忘记 `setSoTimeout` → `read()` 永久阻塞，线程卡死
2. `ServerSocket` / `Socket` 不关闭 → 端口与文件描述符泄漏
3. 在 UI 线程做网络 I/O → 界面卡顿甚至 ANR（网络应在后台线程）
4. 把 `URL` 当 `URI` 用：`URL` 会做 DNS/连接，`URI` 只是标识符
5. UDP 发完不等于对方收到；业务要自己处理重传与乱序
6. 对外网请求不设超时、不做 TLS 校验 → 生产事故高发点
