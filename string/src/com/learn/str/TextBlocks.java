package com.learn.str;

/**
 * 文本块（Java 15+）：多行字符串、缩进剥离、\s、\"、行合并。
 */
public class TextBlocks {

    public static void demo() {
        String json = """
                {
                    "name": "张三",
                    "age": 18
                }
                """;
        System.out.println("JSON 文本块:");
        System.out.print(json);
        System.out.println("长度=" + json.length() + "（结尾自带换行）");

        String aligned = """
                与结束符同缩进
                    本行多缩进 4 格
                """;
        System.out.println("结束符缩进 8 格 → 公共缩进 8 格被剥掉:");
        System.out.println(aligned);

        String indented = """
                    内容比结束符更靠右
                        多出的缩进会保留
                """;
        System.out.println("内容与结束符的缩进差保留下来:");
        System.out.println(indented);

        String merged = """
                第一行\
                紧接第二行\
                结束""";
        System.out.println("反斜杠合并行: [" + merged + "]");

        String spaces = "A\s\sB\s"; // \s 显式表示一个空格，也能保住行尾空格
        System.out.println("普通字符串中的 \\s: [" + spaces + "]");

        String blockWithSpaces = """
                行尾空格默认被去掉      行尾用 \\s 保住 \s
                """;
        System.out.println("文本块行尾的 \\s 效果: [" + blockWithSpaces + "]");

        String quotes = """
                "引号" 无需转义；三个引号可写成 \"\"\" 结束
                """;
        System.out.println(quotes);

        String noTrailingNewline = """
                结束符写在内容行尾，结尾就没有换行""";
        System.out.println("行尾闭合: [" + noTrailingNewline + "]");
    }
}
