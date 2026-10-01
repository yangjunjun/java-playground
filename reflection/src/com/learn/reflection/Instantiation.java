package com.learn.reflection;

/**
 * 反射创建对象：newInstance（已过时）vs getDeclaredConstructor().newInstance()。
 */
public class Instantiation {

    public static class User {
        private final String name;
        private final int age;

        public User() {
            this("默认用户", 0);
        }

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{name='" + name + "', age=" + age + "}";
        }
    }

    public static void demo() {
        Class<User> cls = User.class;
        try {
            // 过时写法（Java 9 标记 @Deprecated）：只能无参构造，异常包装不清
            @SuppressWarnings("deprecation")
            User old = cls.newInstance();
            System.out.println("过时 newInstance(): " + old);

            // 推荐写法：getDeclaredConstructor().newInstance()
            User u1 = cls.getDeclaredConstructor().newInstance();
            System.out.println("无参构造: " + u1);

            // 有参构造：传参数类型
            User u2 = cls.getDeclaredConstructor(String.class, int.class)
                    .newInstance("张三", 20);
            System.out.println("有参构造: " + u2);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
