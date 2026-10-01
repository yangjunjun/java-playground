#!/usr/bin/env bash
# 一键编译并运行 io 模块
set -euo pipefail
cd "$(dirname "$0")"

rm -rf out
mkdir -p out

find src -name '*.java' > /tmp/io-sources.txt
javac -encoding UTF-8 -d out @/tmp/io-sources.txt

java -cp out com.learn.io.Main
