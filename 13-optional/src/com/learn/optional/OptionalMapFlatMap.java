package com.learn.optional;

import java.util.Optional;

/**
 * Optional 的 map / flatMap / filter。
 */
public class OptionalMapFlatMap {

    static class Address {
        final String city;

        Address(String city) {
            this.city = city;
        }
    }

    static class User {
        final String name;
        final Address address; // 可能为 null

        User(String name, Address address) {
            this.name = name;
            this.address = address;
        }

        Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }
    }

    public static void demo() {
        Optional<String> name = Optional.of("java");

        // map：有值则变换，无值保持 empty
        System.out.println("map: " + name.map(String::toUpperCase));
        System.out.println("map 空值: " + Optional.<String>empty().map(String::toUpperCase));

        // filter：不满足谓词则变 empty
        System.out.println("filter 命中: " + name.filter(s -> s.length() > 2));
        System.out.println("filter 未命中: " + name.filter(s -> s.length() > 10));

        // flatMap：函数本身返回 Optional 时避免嵌套 Optional<Optional<...>>
        User u1 = new User("张三", new Address("北京"));
        User u2 = new User("李四", null);

        String city1 = Optional.of(u1).flatMap(User::getAddress).map(a -> a.city).orElse("未知");
        String city2 = Optional.of(u2).flatMap(User::getAddress).map(a -> a.city).orElse("未知");
        System.out.println("flatMap 有地址: " + city1);
        System.out.println("flatMap 无地址: " + city2);

        // 错误示范：map 返回 Optional 会嵌套
        Optional<Optional<Address>> nested = Optional.of(u2).map(User::getAddress);
        System.out.println("map 会嵌套（勿用）: " + nested);
    }
}
