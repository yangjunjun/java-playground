package com.learn.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 元注解：@Target / @Retention / @Documented / @Inherited 如何约束自定义注解。
 */
public class MetaAnnotation {

    /**
     * 只能标在类上；保留到运行时；子类自动继承；Javadoc 中展示。
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    public @interface Role {
        String value();
    }

    /**
     * 仅保留在源码中：编译后丢弃，运行时反射读不到。
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface SourceOnly {
        String value();
    }

    @Role("管理员")
    static class BaseService {
        @SourceOnly("编译期说明")
        public void base() {
            System.out.println("BaseService.base()");
        }
    }

    /** 未标注 @Role，但因为 @Inherited，仍会被视为拥有父类的 @Role */
    static class AdminService extends BaseService {
        public void manage() {
            System.out.println("AdminService.manage()");
        }
    }

    public static void demo() {
        Role onBase = BaseService.class.getAnnotation(Role.class);
        System.out.println("BaseService @Role = " + onBase.value());

        // @Inherited 生效：子类拿到了父类的 @Role
        Role onChild = AdminService.class.getAnnotation(Role.class);
        System.out.println("AdminService 继承到 @Role = " + onChild.value());

        // @Retention(SOURCE) 的注解在运行时不存在
        boolean present = false;
        try {
            present = BaseService.class
                    .getMethod("base")
                    .isAnnotationPresent(SourceOnly.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
        System.out.println("运行时能否读到 @SourceOnly ? " + present + " （SOURCE 保留策略会丢弃）");
    }
}
