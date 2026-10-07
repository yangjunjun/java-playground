package com.learn.modules;

import java.lang.module.ModuleDescriptor;
import java.util.Set;

/**
 * 模块基础（Java 9+）。
 * 演示 Module / ModuleDescriptor / ModuleLayer 的运行时自省能力。
 */
public class ModuleBasics {

    public static void demo() {
        // 当前类所在的模块——classpath 运行时是 unnamed module（unnamed）
        Module current = ModuleBasics.class.getModule();
        System.out.println("当前模块名: " + (current.isNamed() ? current.getName() : "（unnamed 未命名模块）"));
        System.out.println("是否命名模块: " + current.isNamed());
        System.out.println("是否导出包 com.learn.modules: " + current.isExported("com.learn.modules"));

        // 根模块 java.base 的描述符
        Module javaBase = Object.class.getModule();
        ModuleDescriptor desc = javaBase.getDescriptor();
        System.out.println("java.base 描述符: " + desc.name());
        System.out.println("  导出包数量: " + desc.exports().size());
        System.out.println("  是否自动模块: " + desc.isAutomatic());
        System.out.println("  requires 数量: " + desc.requires().size());

        // 列出一些 java.base 导出的包
        int shown = 0;
        for (ModuleDescriptor.Exports e : desc.exports()) {
            System.out.println("  导出包: " + e.source());
            if (++shown >= 5) {
                System.out.println("  ...（共 " + desc.exports().size() + " 个）");
                break;
            }
        }

        // 启动层中已解析的模块数量
        Set<Module> bootModules = ModuleLayer.boot().modules();
        System.out.println("启动层模块数量: " + bootModules.size());
        System.out.println("  含 java.base=" + bootModules.contains(javaBase)
                + ", java.sql=" + bootModules.stream().anyMatch(m -> m.getName().equals("java.sql")));

        // 模块是否打开（open 模块允许反射深访问）
        System.out.println("java.base 是否 open 模块: " + javaBase.getDescriptor().isOpen());
        System.out.println("java.base 导出 java.lang 给 unnamed: "
                + javaBase.isExported("java.lang", current));
        System.out.println("java.base 导出 jdk.internal.misc 给 unnamed: "
                + javaBase.isExported("jdk.internal.misc", current)
                + "（false = 未导出，封装生效）");

        System.out.println("小结: Module API 让你在运行时自省模块图，诊断 --add-opens / 封装问题");
    }
}
