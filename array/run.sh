#!/usr/bin/env bash
# 一键编译并运行 array 模块
set -euo pipefail
cd "$(dirname "$0")"

rm -rf out
mkdir -p out

find src -name '*.java' > /tmp/array-sources.txt
javac -encoding UTF-8 -d out @/tmp/array-sources.txt

java -cp out com.learn.array.Main
