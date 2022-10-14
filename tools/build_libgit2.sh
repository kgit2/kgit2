#!/usr/bin/env zsh
set -e
ROOT=$(pwd)
echo "build libgit2 in $ROOT"
TOOLS_DIR="$(dirname "$0")"
SOURCE_DIR="$ROOT/lib/external/libgit2"
BUIlD_DIR="$SOURCE_DIR/build"

DIST_DIR="$ROOT/lib/libgit2"
LIBSSH2_DIST_DIR="$ROOT/lib/libssh2"

#"$TOOLS_DIR"/build_libssh2.sh

cd "$SOURCE_DIR" || exit
rm -rf "$DIST_DIR" "$BUIlD_DIR"
mkdir -p "$BUIlD_DIR"
cd "$BUIlD_DIR" || exit
cmake "$SOURCE_DIR" \
-DCMAKE_BUILD_TYPE=Release \
-DCMAKE_INSTALL_PREFIX="$DIST_DIR" \
-DCMAKE_OSX_ARCHITECTURES="arm64" \
-DBUILD_SHARED_LIBS=OFF \
-DUSE_SSH=ON \
-DBUILD_TESTS=OFF \
-DCMAKE_PREFIX_PATH=$LIBSSH2_DIST_DIR \

cmake --build . --target install

#unset OPENSSL_ROOT_DIR
#
#cd "$ROOT" || exit
#./gradlew clean
#./gradlew check
