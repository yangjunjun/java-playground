package com.learn.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * ServiceLoader 服务发现（Java 6+，Java 9+ 配合 module-info 的 provides/uses）。
 * 接口 + 多个实现，运行时动态发现，解耦「服务定义」与「服务提供」。
 */
public class ServiceLoaderDemo {

    // ---- 服务接口 ----
    public interface GreetingService {
        String greet(String name);

        String id();
    }

    // ---- 提供者 1 ----
    public static class ChineseGreeting implements GreetingService {
        @Override
        public String greet(String name) {
            return "你好, " + name;
        }

        @Override
        public String id() {
            return "zh-CN";
        }
    }

    // ---- 提供者 2 ----
    public static class EnglishGreeting implements GreetingService {
        @Override
        public String greet(String name) {
            return "Hello, " + name;
        }

        @Override
        public String id() {
            return "en-US";
        }
    }

    public static void demo() {
        // ---- 手动注册（classpath 下最常见写法）----
        List<GreetingService> manual = List.of(new ChineseGreeting(), new EnglishGreeting());
        for (GreetingService s : manual) {
            System.out.println("手动注册: [" + s.id() + "] " + s.greet("Java"));
        }

        // ---- ServiceLoader 动态发现 ----
        // 需要在 META-INF/services/ 下放提供者声明文件，或用 module-info 的 provides
        // 本示例直接用 ServiceLoader.load(GreetingService.class) 并手写注册等价演示：
        // 真正的 ServiceLoader 发现见 multi/ 或下方“模拟发现”
        System.out.println("ServiceLoader 载入接口: " + GreetingService.class.getSimpleName());

        // 模拟 ServiceLoader 的发现顺序（实际由 classpath/module-path 扫描）
        List<GreetingService> discovered = discover();
        System.out.println("发现 " + discovered.size() + " 个提供者:");
        for (GreetingService s : discovered) {
            System.out.println("  -> [" + s.id() + "] " + s.greet("模块"));
        }

        // ---- Java 9+ 模块化下的写法（module-info 语法示意）----
        System.out.println("Java 9+ 模块写法:");
        System.out.println("  服务模块:   uses com.learn.modules.GreetingService;");
        System.out.println("  提供者模块: provides com.learn.modules.GreetingService with com.learn.impl.EnglishGreeting;");
        System.out.println("  调用方:     ServiceLoader.load(GreetingService.class)");

        // ---- 真正的 ServiceLoader.load：扫描 META-INF/services/ 声明 ----
        // 声明文件: src/META-INF/services/com.learn.modules.ServiceLoaderDemo$GreetingService
        // 内容（每行一个实现类全名）:
        //   com.learn.modules.ServiceLoaderDemo$ChineseGreeting
        //   com.learn.modules.ServiceLoaderDemo$EnglishGreeting
        ServiceLoader<GreetingService> loader = ServiceLoader.load(GreetingService.class);
        int count = 0;
        for (GreetingService s : loader) {
            System.out.println("  ServiceLoader 实际发现: [" + s.id() + "] " + s.greet("ServiceLoader"));
            count++;
        }
        System.out.println("  ServiceLoader.load 共发现 " + count + " 个提供者（classpath/META-INF/services）");

        System.out.println("小结: ServiceLoader 解耦接口与实现，模块系统用 uses/provides 声明依赖关系");
    }

    /** 模拟 ServiceLoader 的发现机制：返回全部已知提供者 */
    private static List<GreetingService> discover() {
        List<GreetingService> list = new ArrayList<>();
        list.add(new ChineseGreeting());
        list.add(new EnglishGreeting());
        return list;
    }
}
