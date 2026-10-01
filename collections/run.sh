#!/usr/bin/env bash
# 一键编译并运行 collections 模块
set -euo pipefail
cd "$(dirname "$0")"

rm -rf out
mkdir -p out

find src -name '*.java' > /tmp/collections-sources.txt
javac -encoding UTF-8 -d out @/tmp/collections-sources.txt

java -cp out com.learn.collections.Main
