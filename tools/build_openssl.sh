#!/usr/bin/env zsh
set -e
ROOT=$(pwd)
echo "build openssl in $ROOT"
TOOLS_DIR="$(dirname "$0")"
SOURCE_DIR="$ROOT/lib/external/openssl"
BUIlD_DIR="$SOURCE_DIR/build"
DIST_DIR="$ROOT/lib/openssl"

VERSION="openssl-3.0.7.tar.gz"
DOWNLOAD_URL="https://www.openssl.org/source/$VERSION"
SOURCE_BUNDLE="$ROOT/lib/external/$VERSION"
if [ ! -f "$SOURCE_BUNDLE" ]; then
    export https_proxy=http://127.0.0.1:6152
    export http_proxy=http://127.0.0.1:6152
    export all_proxy=socks5://127.0.0.1:6153
    curl "$DOWNLOAD_URL" -o "$SOURCE_BUNDLE"
fi

if [ -d "$SOURCE_DIR" ]; then
    set -evx
    rm -rf $SOURCE_DIR
    set +evx
fi
mkdir -p "$SOURCE_DIR"
tar -xzf "$SOURCE_BUNDLE" -C "$SOURCE_DIR" --strip-components=1

cd "$SOURCE_DIR" || exit
rm -rf "$DIST_DIR"

#make -s clean
./Configure --prefix="$DIST_DIR" no-ssl3 no-ssl3-method
make -s -j6
make -s install
