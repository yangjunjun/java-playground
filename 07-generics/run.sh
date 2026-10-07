#!/usr/bin/env bash
# 一键编译并运行 generics 模块
set -euo pipefail
cd "$(dirname "$0")"

rm -rf out
mkdir -p out

find src -name '*.java' > /tmp/generics-sources.txt
javac -encoding UTF-8 -d out @/tmp/generics-sources.txt

java -cp out com.learn.generics.Main
