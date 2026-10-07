package com.learn.basics;

/**
 * 控制流程与循环：if/else、传统 switch、for/while/do-while/for-each、break/continue/标签。
 */
public class ControlFlow {

    public static void demo() {
        int score = 85;
        if (score >= 90) {
            System.out.println("if: 优秀");
        } else if (score >= 60) {
            System.out.println("if: 及格");
        } else {
            System.out.println("if: 不及格");
        }

        classicSwitch(3);
        fallThrough();
        allLoops();
        labelledJump();
    }

    private static void classicSwitch(int day) {
        switch (day) {
            case 1:
                System.out.println("switch: 星期一");
                break;
            case 2:
            case 3:
                System.out.println("switch: 周中（多个标签共享逻辑）");
                break;
            case 6:
            case 7:
                System.out.println("switch: 周末");
                break;
            default:
                System.out.println("switch: 其他");
        }

        // String 从 Java 7 起可以作为 switch 的值
        String cmd = "go";
        switch (cmd) {
            case "start":
            case "go":
                System.out.println("switch(String): 启动");
                break;
            case "stop":
                System.out.println("switch(String): 停止");
                break;
            default:
                System.out.println("switch(String): 未知命令");
        }
        System.out.println("（switch 表达式写法见 modern-syntax 模块）");
    }

    private static void fallThrough() {
        System.out.print("fall-through: ");
        int n = 1;
        switch (n) {
            case 1:
                System.out.print("一 ");
                // 故意不写 break：会继续执行 case 2 的代码
            case 2:
                System.out.print("二 ");
                break;
            default:
                System.out.print("其他 ");
        }
        System.out.println("← case 1 缺 break 导致贯穿");
    }

    private static void allLoops() {
        System.out.print("for: ");
        for (int i = 0; i < 3; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        System.out.print("while: ");
        int w = 3;
        while (w > 0) {
            System.out.print(w + " ");
            w--;
        }
        System.out.println();

        System.out.print("do-while: ");
        int d = 0;
        do {
            // 条件一开始就不成立时也至少执行一次
            System.out.print("(执行一次) ");
        } while (d > 0);
        System.out.println();

        System.out.print("for-each: ");
        java.util.List<String> words = java.util.List.of("Java", "Kotlin", "Go");
        for (String word : words) {
            System.out.print(word + " ");
        }
        System.out.println();
    }

    private static void labelledJump() {
        System.out.print("标签跳转: ");
        outer:
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (j == 1) {
                    continue outer;
                }
                if (i == 2) {
                    break outer;
                }
                System.out.print("(" + i + "," + j + ") ");
            }
        }
        System.out.println("← j==1 时跳到外层下一轮，i==2 时结束外层");
    }
}
