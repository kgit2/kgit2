#!/usr/bin/env zsh
ROOT=$(pwd)
echo "build libssh2 in $ROOT"
TOOLS_DIR="$(dirname "$0")"
SOURCE_DIR="$ROOT"/lib/external/openssl
DIST_DIR="$ROOT"/lib/openssl

rm -rf "$DIST_DIR"
cd "$SOURCE_DIR" || exit
make -s clean
./Configure --prefix="$DIST_DIR" no-ssl3 no-ssl3-method
make -s -j6
make -s install
export OPENSSL_ROOT_DIR="$DIST_DIR"
