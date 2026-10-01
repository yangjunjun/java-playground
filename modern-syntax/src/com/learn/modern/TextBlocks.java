package com.learn.modern;

/**
 * 文本块 Text Block（Java 15+ 正式）。
 * 用三引号 """" 包裹多行文本，自动保留缩进结构，适合写 JSON / SQL / HTML。
 */
public class TextBlocks {

    public static void demo() {
        // 改造前：拼接换行，又长又容易写错
        String oldJson = "{\n"
                + "  \"name\": \"张三\",\n"
                + "  \"age\": 18\n"
                + "}";
        System.out.println("旧写法（拼接）:\n" + oldJson);

        // 改造后：文本块，所见即所得
        String newJson = """
                {
                  "name": "张三",
                  "age": 18
                }
                """;
        System.out.println("新写法（文本块）:\n" + newJson);

        // 默认：每行“行尾空格”会被剥离（下面第一行末尾其实打了空格，输出看不到）
        String stripped = """
                A行尾空格被剥掉···   
                B行
                """;
        System.out.println("行尾空格被剥离: [" + stripped + "]");

        // \s 保留一个空格：行尾写 \s\s 即保留两个空格
        String kept = """
                行尾保留两个空格:\s\s|
                """;
        System.out.println("反斜杠s 保留空格: [" + kept + "]");

        // \\ 表示一个真正的反斜杠（写 Windows 路径常用）
        String path = """
                C:\\temp\\file.txt
                """;
        System.out.println("转义反斜杠: " + path);

        // 文本块同样可用 formatted 插值
        String user = "李四";
        String html = """
                <h1>你好, %s</h1>
                """.formatted(user);
        System.out.println("formatted 插值:\n" + html);

        System.out.println("小结：文本块适合多行结构化文本，结尾换行由收尾的 \"\"\" 位置决定");
    }
}
