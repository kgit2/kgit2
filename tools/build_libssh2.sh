#!/usr/bin/env zsh
# build libssh2
cd lib/external/libssh2 || exit
rm -rf build ../../libssh2
mkdir build
cd build || exit
export OPENSSL_ROOT_DIR=/opt/homebrew/opt/openssl@3
cmake .. \
-DCMAKE_INSTALL_PREFIX=../../../libssh2 \
-DCMAKE_OSX_ARCHITECTURES="arm64" \
-DBUILD_SHARED_LIBS=OFF
cmake --build . --target install
