package com.learn.network;

/**
 * 网络编程学习总入口：依次运行各知识点示例。
 * 全部示例仅访问 127.0.0.1 回环，不依赖外部网络。
 * VS Code 中点击 main 上方 Run 即可一键运行。
 */
public class Main {

    public static void main(String[] args) {
        banner("1. InetAddress / URI / URL");
        UrlBasics.demo();

        banner("2. TCP Socket 回声");
        TcpEcho.demo();

        banner("3. UDP 数据报");
        UdpBrief.demo();

        banner("4. HttpClient（Java 11+）");
        HttpClientDemo.demo();

        banner("5. 网络工具与常识");
        NetworkTools.demo();

        banner("6. 超时处理");
        SocketTimeoutDemo.demo();

        System.out.println();
        System.out.println("全部示例运行完毕。");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("========== " + title + " ==========");
    }
}
