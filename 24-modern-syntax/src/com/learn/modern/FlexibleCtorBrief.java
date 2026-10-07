package com.learn.modern;

/**
 * 构造器中的灵活语句（Java 22+ 正式）。
 * 在 super()/this() 之前可以写普通语句，方便先做校验或参数处理再调用父类构造器。
 */
public class FlexibleCtorBrief {

    static class Base {
        Base(String name) {
            System.out.println("  Base 构造: name=" + name);
        }
    }

    static class User extends Base {
        final String normalized;

        User(String raw) {
            // Java 22 之前：super() 必须是第一条语句，想校验只能写成静态方法内联
            // Java 22+：可以先写任意语句，再调用 super()
            String trimmed = raw.trim();
            if (trimmed.isEmpty()) {
                throw new IllegalArgumentException("名字不能为空");
            }
            super(trimmed.toLowerCase());
            this.normalized = trimmed;
            System.out.println("  User 构造: normalized=" + normalized);
        }
    }

    // 改造前的笨办法：把校验塞进静态方法，再传给 super()
    static class OldUser extends Base {
        OldUser(String raw) {
            super(validate(raw));
        }

        private static String validate(String raw) {
            String t = raw.trim();
            if (t.isEmpty()) {
                throw new IllegalArgumentException("名字不能为空");
            }
            return t.toLowerCase();
        }
    }

    public static void demo() {
        System.out.println("Java 22+ 新写法（super 前可写语句）:");
        new User("  Alice  ");

        System.out.println("旧写法（校验塞进静态方法）:");
        new OldUser("  Bob  ");

        System.out.println("异常情况:");
        try {
            new User("   ");
        } catch (IllegalArgumentException e) {
            System.out.println("  捕获 -> " + e.getMessage());
        }

        System.out.println("小结：Java 22+ 允许 super()/this() 前写普通语句，代码更自然");
    }
}
