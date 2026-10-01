#!/usr/bin/env bash
# 一键编译并运行 i18n 模块
set -euo pipefail
cd "$(dirname "$0")"

rm -rf out
mkdir -p out

find src -name '*.java' > /tmp/i18n-sources.txt
javac -encoding UTF-8 -d out @/tmp/i18n-sources.txt

# src 需要在 classpath 上，ResourceBundle 才能加载 src/i18n/*.properties
java -cp out:src com.learn.i18n.Main
