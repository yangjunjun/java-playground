package main;

import com.learn.greet.Greeter;

/**
 * app 模块入口：演示跨模块调用。
 * app 通过 requires greet 获得对 com.learn.greet 包的访问权。
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("===== 多模块 JPMS 演示 =====");
        System.out.println("当前模块: " + Main.class.getModule().getName());

        Greeter greeter = new Greeter();
        System.out.println(greeter.greet("模块化 Java"));
        System.out.println(greeter.version());

        // 检查模块是否被正确识别为命名模块
        Module greetModule = Greeter.class.getModule();
        System.out.println("Greeter 所在模块: " + greetModule.getName()
                + ", 是否命名模块: " + greetModule.isNamed());
        System.out.println("app 是否读取 greet: "
                + Main.class.getModule().canRead(greetModule));

        // 尝试访问 greet 未导出的包会编译失败（见 README）
        System.out.println("封装生效: app 只能用 greet 导出的 com.learn.greet 包");

        System.out.println("===== 多模块演示结束 =====");
    }
}
