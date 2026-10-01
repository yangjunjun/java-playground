#!/usr/bin/env bash
# 编译并运行全部知识点模块，任一失败则退出码非 0
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

failed=()
ran=0

for dir in */ ; do
  topic="${dir%/}"
  [ "$topic" = "scripts" ] && continue
  [ -f "$topic/run.sh" ] || continue
  echo ""
  echo "################################################################"
  echo "##  $topic"
  echo "################################################################"
  if bash "$topic/run.sh"; then
    ran=$((ran + 1))
  else
    failed+=("$topic")
  fi
done

echo ""
echo "========================================"
echo "运行模块数: $ran"
if [ ${#failed[@]} -eq 0 ]; then
  echo "全部通过 ✓"
else
  echo "失败模块: ${failed[*]}"
  exit 1
fi
