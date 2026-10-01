#!/usr/bin/env bash
# 编译并运行真正的双模块 JPMS 示例：app 模块 requires greet 模块
set -euo pipefail
cd "$(dirname "$0")"

rm -rf out
mkdir -p out/greet out/app

echo "--- 1. 编译 greet 模块 ---"
javac -encoding UTF-8 -d out/greet $(find greet -name '*.java')

echo "--- 2. 编译 app 模块（--module-path 指向 out，才能 requires greet）---"
javac -encoding UTF-8 --module-path out -d out/app $(find app -name '*.java')

echo "--- 3. 运行：java --module-path out -m app/main.Main ---"
java --module-path out -m app/main.Main

echo "--- 4. 查看模块描述符 ---"
java --module-path out --describe-module app
java --module-path out --describe-module greet
