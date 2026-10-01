package com.learn.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * 反射读取注解：getAnnotation / isAnnotationPresent / getDeclaredAnnotations。
 */
public class AnnotationReflection {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Api {
        String path();
        String method() default "GET";
    }

    static class OrderController {

        @Api(path = "/orders", method = "GET")
        public void listOrders() {
            System.out.println("查询订单列表");
        }

        @Api(path = "/orders/{id}")
        public void getOrder() {
            System.out.println("查询单个订单");
        }

        public void health() {
            System.out.println("健康检查（无注解）");
        }
    }

    public static void demo() {
        OrderController controller = new OrderController();

        for (Method method : OrderController.class.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Api.class)) {
                Api api = method.getAnnotation(Api.class);
                System.out.printf("%s  ->  %s %s%n", method.getName(), api.method(), api.path());
                invoke(controller, method);
            } else {
                System.out.println(method.getName() + "  ->  (无 @Api，跳过)");
            }
        }
    }

    private static void invoke(Object target, Method method) {
        try {
            method.invoke(target);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
