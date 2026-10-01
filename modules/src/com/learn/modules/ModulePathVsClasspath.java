package com.learn.modules;

import java.lang.module.ModuleDescriptor;
import java.util.Set;

/**
 * 模块路径 vs 类路径（Java 9+）。
 * -classpath：一切都进 unnamed module，封装全开、依赖关系不可见。
 * --module-path：每个 jar/目录成为一个命名模块（或自动模块），编译期校验依赖。
 */
public class ModulePathVsClasspath {

    public static void demo() {
        // ---- 当前运行方式 ----
        Module current = ModulePathVsClasspath.class.getModule();
        boolean named = current.isNamed();
        System.out.println("当前运行模式: " + (named ? "模块路径（命名模块）" : "类路径（unnamed 模块）"));
        System.out.println("本模块名: " + (named ? current.getName() : "unnamed"));

        // ---- classpath 的问题 ----
        System.out.println("—— -classpath 的问题 ——");
        System.out.println("1) 依赖关系不可见：编译器不知道 jar 之间谁依赖谁");
        System.out.println("2) 封装失效：任何 public 类都能被反射调用");
        System.out.println("3) 类路径地狱：同一 jar 多版本共存时行为不确定");
        System.out.println("4) 启动慢：需要扫描整个 classpath");

        // ---- module-path 的好处 ----
        System.out.println("—— --module-path 的好处 ——");
        System.out.println("1) 编译期校验 requires：缺依赖直接报错");
        System.out.println("2) 封装生效：未 exports 的包外部不可见");
        System.out.println("3) 可靠配置：模块图在启动时一次性解析");
        System.out.println("4) 可裁剪：jlink 只打包用到的模块，镜像更小");

        // ---- 自动模块（automatic module）----
        System.out.println("—— 自动模块 ——");
        System.out.println("普通 jar 放上 --module-path 会成为自动模块：");
        System.out.println("  - 模块名从 jar 文件名推导（或 MANIFEST 的 Automatic-Module-Name）");
        System.out.println("  - 自动导出所有包（封装失效）");
        System.out.println("  - 用于迁移：老 jar 无需 module-info 也能被模块使用");

        // ---- 运行时自省：启动层模块清单 ----
        Set<Module> modules = ModuleLayer.boot().modules();
        System.out.println("启动层模块数量: " + modules.size());
        System.out.println("前 8 个模块:");
        modules.stream()
                .sorted((a, b) -> a.getName().compareTo(b.getName()))
                .limit(8)
                .forEach(m -> {
                    ModuleDescriptor d = m.getDescriptor();
                    String kind = d.isAutomatic() ? "（自动模块）"
                            : m.getName().startsWith("java.") ? "（平台模块）" : "";
                    System.out.println("  " + m.getName() + kind);
                });

        // ---- 检查某个模块是否可读 ----
        Module javaBase = Object.class.getModule();
        System.out.println("当前模块能否读取 java.base: " + current.canRead(javaBase));

        System.out.println("小结: 新项目优先 --module-path；迁移期可用自动模块过渡（见 multi/ 示例）");
    }
}
