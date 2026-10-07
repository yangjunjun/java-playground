#!/usr/bin/env bash
# 一键编译并运行 modules 模块（单模块语言示例 + 多模块 JPMS 示例）
set -euo pipefail
cd "$(dirname "$0")"

rm -rf out
mkdir -p out

find src -name '*.java' > /tmp/modules-sources.txt
javac -encoding UTF-8 -d out @/tmp/modules-sources.txt

# ServiceLoader 需要 META-INF/services 声明文件也进 classpath
cp -r src/META-INF out/

java -cp out com.learn.modules.Main

echo ""
echo "================================================"
echo "接下来运行真正的双模块 JPMS 示例（multi/）"
echo "================================================"
bash ./multi/run.sh
