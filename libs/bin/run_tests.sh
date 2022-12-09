#!/usr/bin/env zsh
set -e
ROOT=$(pwd)
echo "build `tests` and test it in $ROOT"
TOOLS_DIR="$(dirname "$0")"
SOURCE_DIR="$ROOT/lib/tests"
BUIlD_DIR="$SOURCE_DIR/build"

rm -rf "$BUIlD_DIR"
mkdir -p "$BUIlD_DIR"
cd "$BUIlD_DIR"
cmake ..
make
./tests

if [ $? != 0 ]; then
    exit 1
fi
