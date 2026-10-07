package com.learn.reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 运行时注解：getAnnotation / isAnnotationPresent。
 * 前提：@Retention(RetentionPolicy.RUNTIME)。
 */
public class AnnotationReflect {

    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Route {
        String value();
    }

    @Route("/users")
    public static class UserController {

        @Route("/list")
        public void list() {
        }

        @Route("/create")
        public void create() {
        }
    }

    public static void demo() {
        // 类上的注解
        Class<UserController> cls = UserController.class;
        if (cls.isAnnotationPresent(Route.class)) {
            Route route = cls.getAnnotation(Route.class);
            System.out.println("类路由: " + route.value());
        }

        // 方法上的注解
        for (var m : cls.getDeclaredMethods()) {
            Route route = m.getAnnotation(Route.class);
            if (route != null) {
                System.out.println("方法 " + m.getName() + " -> " + route.value());
            }
        }
    }
}
