package com.learn.modules;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 封装演示（Java 9+）。
 * 对比：classpath（unnamed 模块）下 public 一律可反射访问；
 * 命名模块中未导出的包对其他模块不可见，反射也需要 opens。
 */
public class PackageEncapsulationDemo {

    // 公开类型，但所在包是否导出决定了外部可见性
    public static class PublicApi {
        public String hello() {
            return "来自 PublicApi";
        }
    }

    // 模拟“内部实现”：public 但应视为包私有
    public static class InternalImpl {
        public String secret() {
            return "内部实现，不应被外部依赖";
        }

        private String hidden() {
            return "私有方法";
        }
    }

    public static void demo() {
        // ---- 场景 1：classpath（unnamed 模块）下，public 一律可见 ----
        // 本示例运行在 classpath 上，所有类都在 unnamed module 里
        Module unnamed = PackageEncapsulationDemo.class.getModule();
        System.out.println("当前模块: " + (unnamed.isNamed() ? unnamed.getName() : "unnamed（classpath）"));
        System.out.println("unnamed 模块对所有包都自动导出: " + unnamed.isExported("com.learn.modules"));

        PublicApi api = new PublicApi();
        System.out.println("直接调用 PublicApi: " + api.hello());
        InternalImpl impl = new InternalImpl();
        System.out.println("直接调用 InternalImpl（也能调，因为同模块）: " + impl.secret());

        // ---- 场景 2：反射在 unnamed 模块下几乎不受限 ----
        try {
            Method m = InternalImpl.class.getDeclaredMethod("hidden");
            m.setAccessible(true); // unnamed 模块下通常直接成功
            System.out.println("反射调用私有方法: " + m.invoke(impl));
        } catch (Exception e) {
            System.out.println("反射失败: " + e);
        }

        // ---- 场景 3：命名模块下的差异（概念说明）----
        System.out.println("—— 命名模块下的差异（概念，需 multi/ 示例验证）——");
        System.out.println("1) 未 exports 的包：其他模块编译期就报错，找不到类型");
        System.out.println("2) exports 但未 opens 的包：可编译可调用公开方法，但反射 setAccessible 会被拒");
        System.out.println("3) opens 的包：允许反射深入（框架如 Spring/Jackson 常需 opens）");
        System.out.println("4) 对比运行 multi/ 示例：app 只能用 greet 导出的 com.learn.greet 包");

        // ---- 场景 4：查看 String 所在模块的导出情况 ----
        Module javaBase = String.class.getModule();
        System.out.println("java.lang 已导出（故 String 可见）: "
                + javaBase.isExported("java.lang", unnamed));
        System.out.println("jdk.internal.misc 未导出: "
                + javaBase.isExported("jdk.internal.misc", unnamed)
                + "（false = 其他模块不可见）");

        System.out.println("小结: 封装 = 导出决定可见性，opens 决定反射深度；classpath 全开，模块系统才有封装");
    }
}
