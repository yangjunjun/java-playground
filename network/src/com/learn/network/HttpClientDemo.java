package com.learn.network;

import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * java.net.http.HttpClient（Java 11+）演示。
 * <p>
 * 为了不访问互联网，这里先在 127.0.0.1 起一个 JDK 自带的
 * {@code com.sun.net.httpserver.HttpServer}，再用 HttpClient 访问它。
 */
public class HttpClientDemo {

    public static void demo() {
        HttpServer server = null;
        try {
            // ---- 本地回环 HTTP 服务（纯 JDK，无外部依赖）----
            server = HttpServer.create(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0), 0);
            server.createContext("/hello", exchange -> {
                byte[] body = "你好，HttpClient！".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
                exchange.sendResponseHeaders(200, body.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(body);
                }
            });
            server.createContext("/echo", exchange -> {
                byte[] body = exchange.getRequestBody().readAllBytes();
                exchange.sendResponseHeaders(200, body.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(body);
                }
            });
            server.start();
            int port = server.getAddress().getPort();
            System.out.println("本地 HTTP 服务已启动: http://127.0.0.1:" + port);

            // ---- HttpClient：Java 11+ 标准 HTTP 客户端 ----
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(2))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            // GET 请求：用 builder 构造
            HttpRequest get = HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:" + port + "/hello"))
                    .timeout(Duration.ofSeconds(2))
                    .header("Accept", "text/plain")
                    .GET()
                    .build();
            HttpResponse<String> resp = client.send(get, HttpResponse.BodyHandlers.ofString());
            System.out.println("GET /hello → 状态 " + resp.statusCode() + ", 响应体: " + resp.body());

            // POST 请求：带请求体
            HttpRequest post = HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:" + port + "/echo"))
                    .timeout(Duration.ofSeconds(2))
                    .header("Content-Type", "text/plain; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString("来自客户端的问候"))
                    .build();
            HttpResponse<String> echoed = client.send(post, HttpResponse.BodyHandlers.ofString());
            System.out.println("POST /echo → 状态 " + echoed.statusCode() + ", 响应体: " + echoed.body());

            // 真实项目调用互联网接口时，务必设置超时与 TLS，并处理 IOException / InterruptedException
            // 异步用法（Java 11+）：
            //   client.sendAsync(get, HttpResponse.BodyHandlers.ofString())
            //         .thenApply(HttpResponse::body)
            //         .thenAccept(System.out::println);

            // WebSocket（Java 11+）仅演示 API 形态，不真实握手：
            //   HttpClient.newHttpClient()
            //       .newWebSocketBuilder()
            //       .buildAsync(URI.create("wss://example.com/ws"), listener)
            //       .join();
            System.out.println("WebSocket：HttpClient.newWebSocketBuilder() 提供异步 WS 客户端（此处仅说明 API）");
        } catch (Exception e) {
            System.out.println("HttpClient 演示异常: " + e);
        } finally {
            if (server != null) {
                server.stop(0);
            }
        }
    }
}
