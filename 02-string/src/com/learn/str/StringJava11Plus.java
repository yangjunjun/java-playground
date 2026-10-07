package com.learn.str;

/**
 * Java 11+ 文本 API：strip / isBlank / lines / repeat / indent / transform / stripIndent / translateEscapes。
 */
public class StringJava11Plus {

    public static void demo() {
        String raw = "  你好，世界  ";
        System.out.println("trim=[" + raw.trim() + "] 与 strip=[" + raw.strip() + "] 效果相近");
        System.out.println("stripLeading=[" + raw.stripLeading() + "]  stripTrailing=[" + raw.stripTrailing() + "]");

        // 全角空格 U+3000 属于 Character.isWhitespace，trim 去不掉而 strip 可以
        String wide = "\u3000两侧全角空格\u3000";
        System.out.println("全角空格: trim 后长度=" + wide.trim().length()
                + ", strip 后长度=" + wide.strip().length());
        // U+00A0 不换行空格不算 isWhitespace，strip 也去不掉
        String nbsp = "a\u00A0b";
        System.out.println("U+00A0 被 strip 保留: [" + nbsp.strip() + "] 长度=" + nbsp.strip().length());

        System.out.println("\"  \".isBlank()=" + "  \t".isBlank() + ", \"\".isBlank()=" + "".isBlank());
        System.out.println("\"a\".isBlank()=" + "a".isBlank());

        System.out.println("repeat: " + "ab".repeat(3));

        System.out.println("lines:");
        "第一行\n第二行\n第三行".lines().forEach(line -> System.out.println("  " + line));

        System.out.println("indent(+2):");
        System.out.println("int x = 1;".indent(2) + "int y = 2;".indent(2));

        String transformed = "hello".transform(String::toUpperCase).transform(s -> s + "!");
        System.out.println("transform: " + transformed);

        String messy = "    第一行\n        第二行\n    第三行";
        System.out.println("stripIndent 前: [" + messy + "]");
        System.out.println("stripIndent 后: [" + messy.stripIndent() + "]");
        // 以换行结尾时 stripIndent 跳过去缩进（只清行尾空白），注意与文本块的差异
        String endsWithNl = "    a\n    b\n";
        System.out.println("以换行结尾 stripIndent: [" + endsWithNl.stripIndent() + "]");

        String escapes = "a\\nb\\tc"; // 内容是两个字符的 \n，而非换行符
        System.out.println("translateEscapes 前: [" + escapes + "]  length=" + escapes.length());
        System.out.println("translateEscapes 后（含真实换行与制表符）:");
        System.out.println(escapes.translateEscapes());
        System.out.println("translateEscapes 后 length=" + escapes.translateEscapes().length());
    }
}
