package com.learn.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 重复注解（Java 8）：@Repeatable + 容器注解。
 */
public class RepeatableAnnotation {

    /** 允许在同一元素上重复使用 */
    @Repeatable(TaskTags.class)
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TaskTag {
        String value();
    }

    /** 容器注解：value() 必须是被重复注解的数组 */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TaskTags {
        TaskTag[] value();
    }

    @TaskTag("导入")
    @TaskTag("清洗")
    @TaskTag("导出")
    static class DataPipeline {
    }

    public static void demo() {
        // 方式一：拿到容器注解，再展开数组
        TaskTags container = DataPipeline.class.getAnnotation(TaskTags.class);
        System.out.println("容器方式读到标签数 = " + container.value().length);
        for (TaskTag tag : container.value()) {
            System.out.println("  - " + tag.value());
        }

        // 方式二：直接 getAnnotationsByType，自动合并/展开
        TaskTag[] tags = DataPipeline.class.getAnnotationsByType(TaskTag.class);
        System.out.println("getAnnotationsByType 读到标签数 = " + tags.length);
    }
}
