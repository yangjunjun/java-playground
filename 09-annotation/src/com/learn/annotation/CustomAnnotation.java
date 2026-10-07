package com.learn.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解：@interface 定义 + 默认值 + 使用方式。
 */
public class CustomAnnotation {

    /** 标记业务操作的标签注解 */
    @Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyLabel {
        /** 标签名称；只有 value 时可省略 "value =" */
        String value() default "";

        /** 优先级，数值越大越重要 */
        int priority() default 0;

        /** 可打多个标签 */
        String[] tags() default {};
    }

    @MyLabel(value = "用户服务", tags = { "核心", "业务" })
    static class UserService {

        @MyLabel("登录")
        public void login() {
            System.out.println("[方法] 执行登录");
        }

        @MyLabel(value = "下单", priority = 2, tags = "交易")
        public void createOrder() {
            System.out.println("[方法] 创建订单");
        }

        @MyLabel(value = "用户ID", priority = 1)
        public String userId = "u-1001";
    }

    public static void demo() {
        UserService service = new UserService();
        service.login();
        service.createOrder();

        MyLabel classLabel = UserService.class.getAnnotation(MyLabel.class);
        System.out.println("类上注解 value = " + classLabel.value()
                + ", tags = " + java.util.Arrays.toString(classLabel.tags()));

        MyLabel fieldLabel = getFieldLabel();
        System.out.println("字段上注解 value = " + fieldLabel.value()
                + ", priority = " + fieldLabel.priority());
    }

    private static MyLabel getFieldLabel() {
        try {
            return UserService.class.getField("userId").getAnnotation(MyLabel.class);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }
}
