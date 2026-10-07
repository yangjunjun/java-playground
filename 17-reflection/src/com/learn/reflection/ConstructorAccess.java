package com.learn.reflection;

import java.lang.reflect.Constructor;

/**
 * 构造器反射：getConstructors / getDeclaredConstructors、私有构造、单例破坏演示。
 */
public class ConstructorAccess {

    public static class Config {
        private final String key;
        private final String value;

        private Config(String key, String value) {   // 私有构造（单例/工厂常用）
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Config{" + key + "=" + value + "}";
        }
    }

    public static void demo() {
        try {
            // getConstructors 只返回 public；getDeclaredConstructors 返回全部
            Class<Config> cls = Config.class;
            System.out.println("public 构造器数量: " + cls.getConstructors().length);
            System.out.println("全部声明构造器数量: " + cls.getDeclaredConstructors().length);

            // 私有构造：setAccessible 后照样能用
            Constructor<Config> ctor = cls.getDeclaredConstructor(String.class, String.class);
            ctor.setAccessible(true);
            Config cfg = ctor.newInstance("timeout", "30s");
            System.out.println("私有构造创建: " + cfg);

            // 构造器参数元信息
            System.out.println("参数类型: " + java.util.Arrays.toString(ctor.getParameterTypes()));
            System.out.println("参数个数: " + ctor.getParameterCount());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
