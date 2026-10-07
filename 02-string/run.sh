#!/usr/bin/env bash
# 一键编译并运行 string 模块
set -euo pipefail
cd "$(dirname "$0")"

rm -rf out
mkdir -p out

find src -name '*.java' > /tmp/string-sources.txt
javac -encoding UTF-8 -d out @/tmp/string-sources.txt

java -cp out com.learn.str.Main
