package com.learn.stream;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Collectors：收集为集合、连接、分组、分区与聚合。
 */
public class CollectorsDemo {

    static class Employee {
        final String name;
        final String dept;
        final int salary;

        Employee(String name, String dept, int salary) {
            this.name = name;
            this.dept = dept;
            this.salary = salary;
        }
    }

    public static void demo() {
        List<String> words = List.of("java", "stream", "api", "java");

        // toList / toSet / toUnmodifiableList（Java 10+）
        System.out.println("toList: " + words.stream().collect(Collectors.toList()));
        System.out.println("toSet: " + words.stream().collect(Collectors.toSet()));
        System.out.println("toUnmodifiableList: " + words.stream().collect(Collectors.toUnmodifiableList()));

        // joining
        System.out.println("joining: " + words.stream().collect(Collectors.joining(", ", "[", "]")));

        // toMap：重复键会抛 IllegalStateException，需给 mergeFunction
        Map<String, Integer> lengthMap = words.stream()
                .distinct()
                .collect(Collectors.toMap(s -> s, String::length));
        System.out.println("toMap: " + lengthMap);

        // 分组
        List<Employee> staff = List.of(
                new Employee("张三", "研发", 20000),
                new Employee("李四", "研发", 18000),
                new Employee("王五", "市场", 12000),
                new Employee("赵六", "市场", 15000));

        Map<String, List<Employee>> byDept = staff.stream()
                .collect(Collectors.groupingBy(e -> e.dept));
        System.out.println("groupingBy 部门: " + byDept.keySet());

        Map<String, Long> countByDept = staff.stream()
                .collect(Collectors.groupingBy(e -> e.dept, Collectors.counting()));
        System.out.println("groupingBy + counting: " + countByDept);

        Map<String, Integer> sumByDept = staff.stream()
                .collect(Collectors.groupingBy(e -> e.dept, Collectors.summingInt(e -> e.salary)));
        System.out.println("groupingBy + summingInt: " + sumByDept);

        // 分区：按条件分成 true / false 两组
        Map<Boolean, List<String>> partitioned = words.stream()
                .collect(Collectors.partitioningBy(s -> s.length() > 3));
        System.out.println("partitioningBy: " + partitioned);

        // teeing（Java 12+）：一次遍历得到两个收集结果
        String teeing = staff.stream()
                .collect(Collectors.teeing(
                        Collectors.counting(),
                        Collectors.summingInt(e -> e.salary),
                        (count, total) -> "人数=" + count + " 薪资合计=" + total));
        System.out.println("teeing: " + teeing);

        // Map.Entry 遍历
        lengthMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.println("  Entry: " + e.getKey() + " -> " + e.getValue()));
    }
}
