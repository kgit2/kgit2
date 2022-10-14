#!/usr/bin/env zsh
set -e
ROOT=$(pwd)
echo "build libssh2 in $ROOT"
TOOLS_DIR="$(dirname "$0")"
SOURCE_DIR="$ROOT/lib/external/libssh2"
BUIlD_DIR="$SOURCE_DIR/build"
DIST_DIR="$ROOT/lib/libssh2"

# build openssl
#"$TOOLS_DIR"/build_openssl.sh

VERSION="libssh2-1.10.0.tar.gz"
SOURCE_BUNDLE="$ROOT/lib/external/$VERSION"
DOWNLOAD_URL="https://www.libssh2.org/download/$VERSION"
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

"${SOURCE_DIR}"/configure --prefix="$DIST_DIR" \
--disable-silent-rules \
--disable-examples-build \
--with-libz \
--with-crypto=openssl \

make install -j6 -C "$SOURCE_DIR"

echo "Build libssh2 Successful"
#mkdir -p "$BUIlD_DIR"
#cd "$BUIlD_DIR" || exit
#cmake "$SOURCE_DIR" \
#-DCMAKE_BUILD_TYPE=Release \
#-DCMAKE_INSTALL_PREFIX="$DIST_DIR" \
#-DCMAKE_OSX_ARCHITECTURES="arm64" \
#-DENABLE_ZLIB_COMPRESSION=ON \
#-DCRYPTO_BACKEND=OpenSSL \
#-DBUILD_SHARED_LIBS=OFF \
#
#cmake "$SOURCE_DIR" \
#-DCMAKE_BUILD_TYPE=Release \
#-DCMAKE_INSTALL_PREFIX="$DIST_DIR" \
#-DCMAKE_OSX_ARCHITECTURES="arm64" \
#-DENABLE_ZLIB_COMPRESSION=ON \
#-DCRYPTO_BACKEND=OpenSSL \
#-DBUILD_SHARED_LIBS=ON \
#
#cmake --build . --target install
