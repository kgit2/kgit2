#!/usr/bin/env zsh
# build libgit2
ROOT=$(pwd)
"$ROOT"/tools/build_libssh2.sh
cd "$ROOT"/lib/external/libgit2 || exit
rm -rf build ../../libgit2
mkdir build
cd build || exit
export OPENSSL_ROOT_DIR=/opt/homebrew/opt/openssl@3
cmake .. \
-DCMAKE_INSTALL_PREFIX=../../../libgit2 \
-DCMAKE_OSX_ARCHITECTURES="arm64" \
-DBUILD_SHARED_LIBS=OFF \
-DUSE_SSH=ON \
-DCMAKE_PREFIX_PATH=/Users/bppleman/floater/kgit2/lib/libssh2 \
-DCMAKE_PREFIX_PATH=/opt/homebrew/opt/openssl@3 \
-DBUILD_EXAMPLES=YES \
-DBUILD_TESTS=OFF
cmake --build . --target install

headers="git2.h"
staticLibraries="libgit2.a libssh2.a"
libraryPaths="$ROOT/lib/libgit2/lib $ROOT/lib/libssh2/lib"
compilerOpts="-I$ROOT/lib/libgit2/include"
export PKG_CONFIG_PATH="/opt/homebrew/opt/openssl@3/lib/pkgconfig":$PKG_CONFIG_PATH
linkerOpts="$(pkg-config --libs openssl) $(pkg-config --libs zlib) -liconv"

echo "headers = $headers" > $ROOT/lib/libgit2.def
echo "staticLibraries = $staticLibraries" >> $ROOT/lib/libgit2.def
echo "libraryPaths = $libraryPaths" >> $ROOT/lib/libgit2.def
echo "compilerOpts = $compilerOpts" >> $ROOT/lib/libgit2.def
echo "linkerOpts = $linkerOpts" >> $ROOT/lib/libgit2.def

unset OPENSSL_ROOT_DIR

cd $ROOT
./gradlew clean
./gradlew check
