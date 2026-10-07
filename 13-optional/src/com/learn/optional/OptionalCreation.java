package com.learn.optional;

import java.util.Optional;

/**
 * Optional 的创建方式：of / ofNullable / empty。
 */
public class OptionalCreation {

    public static void demo() {
        // Optional.of：值必须非 null，传 null 会立刻 NPE
        Optional<String> present = Optional.of("hello");
        System.out.println("of: " + present);

        // Optional.ofNullable：允许 null，null 时得到 empty
        Optional<String> maybe = Optional.ofNullable(null);
        System.out.println("ofNullable(null): " + maybe + "，isEmpty: " + maybe.isEmpty());

        // Optional.empty：显式构造“无值”
        Optional<String> empty = Optional.empty();
        System.out.println("empty: " + empty + "，isPresent: " + empty.isPresent());

        try {
            Optional.of(null);
        } catch (NullPointerException e) {
            System.out.println("of(null) 抛 NPE: " + e.getClass().getSimpleName());
        }
    }
}
