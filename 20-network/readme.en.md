# Networking

Java provides TCP/UDP networking through `java.net`, and a modern HTTP client through `java.net.http` since Java 11. This module **only ever talks to the 127.0.0.1 loopback address** and does not depend on an external network: the TCP/UDP echo and the local HTTP server are all set up in-process.

## 1. InetAddress and InetSocketAddress

`InetAddress` represents an IP address; `InetSocketAddress` = IP + port:

```java
InetAddress local = InetAddress.getLocalHost();
InetAddress loopback = InetAddress.getByName("localhost"); // loopback address

InetSocketAddress addr = new InetSocketAddress("127.0.0.1", 8080);
// port 0 = let the system assign an ephemeral port
```

## 2. URI and URL

A `URI` is a generic resource identifier (immutable, parseable); a `URL` is the subset that "can open a connection":

```java
URI uri = new URI("https://example.com:8443/docs/java?lang=zh#intro");
uri.getScheme();   // https
uri.getHost();     // example.com
uri.getPath();     // /docs/java
uri.getQuery();    // lang=zh
```

## 3. TCP: Socket / ServerSocket

TCP is a connection-oriented, reliable byte stream. Typical flow:

```java
// server
try (ServerSocket server = new ServerSocket(0, 8, InetAddress.getByName("127.0.0.1"))) {
    Socket conn = server.accept();          // blocks, waiting for a connection
    // read InputStream / write OutputStream ...
}

// client
try (Socket client = new Socket()) {
    client.connect(new InetSocketAddress("127.0.0.1", port), 2000);
    client.setSoTimeout(2000);              // read timeout
    client.getOutputStream().write(data);
}
```

After `accept()`, each connection is usually handed to its own thread or to an NIO `Selector`.

## 4. UDP: DatagramSocket

UDP is connectionless and unreliable — packets may be lost or reordered — but it has low latency:

```java
try (DatagramSocket sock = new DatagramSocket(0, InetAddress.getByName("127.0.0.1"))) {
    sock.send(new DatagramPacket(data, data.length, dest, port));
    sock.receive(pkt);
}
```

## 5. HttpClient (Java 11+)

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

Use `sendAsync` for asynchronous calls; WebSocket uses `client.newWebSocketBuilder()` (Java 11+).
This module starts a stub server on the loopback with the JDK's own `com.sun.net.httpserver.HttpServer` so that `HttpClient` can perform a real round trip.

## 6. Ports and Timeouts

| Concept | Description |
|------|------|
| Well-known ports | 0–1023 (80 HTTP, 443 HTTPS) |
| Registered ports | 1024–49151 |
| Dynamic ports | 49152–65535; binding to 0 assigns from this range |
| connect timeout | `socket.connect(addr, timeout)` / `HttpClient.connectTimeout` |
| read timeout | `socket.setSoTimeout(ms)`; on timeout throws `SocketTimeoutException` |

**Real network code must set timeouts**, otherwise threads can be blocked on I/O forever.

## 7. Real-World Considerations

- Timeouts: set them for connect, read, and write
- TLS: use HTTPS for external calls, validate certificates, never `TrustAllCerts`
- Resource cleanup: use try-with-resources for `Socket` / `InputStream` / `OutputStream`
- Limit response body size to keep a maliciously large response from exhausting memory
- Do not use `isReachable()` for health checks (it relies on ICMP and is often blocked by firewalls)

## 8. Running This Module

**VS Code**: open `src/com/learn/network/Main.java` and click **Run** above `main`.

**Command line**:

```bash
./run.sh
```

## Practice Files

| File | Contents |
|------|------|
| `Main.java` | main entry point, runs all examples in order |
| `UrlBasics.java` | InetAddress / InetSocketAddress / URI / URL |
| `TcpEcho.java` | TCP Socket/ServerSocket echo |
| `UdpBrief.java` | UDP DatagramSocket datagrams |
| `HttpClientDemo.java` | HttpClient request building and local round trips, WebSocket API intro |
| `NetworkTools.java` | port ranges, loopback probing, InetSocketAddress |
| `SocketTimeoutDemo.java` | read timeout / bounded waiting on connection failure |

## Common Pitfalls

1. Forgetting `setSoTimeout` leaves `read()` blocked forever and the thread stuck
2. Not closing `ServerSocket` / `Socket` leaks ports and file descriptors
3. Doing network I/O on the UI thread freezes the UI or triggers an ANR (network calls belong on a background thread)
4. Treating `URL` as `URI`: `URL` performs DNS/connect, `URI` is just an identifier
5. Sending a UDP packet does not mean the peer received it; applications must handle retransmission and reordering themselves
6. Making outbound requests without timeouts or TLS validation is a leading cause of production incidents
