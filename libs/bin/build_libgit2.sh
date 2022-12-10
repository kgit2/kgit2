#!/usr/bin/env zsh
set -evx
export ROOT=$(pwd)
export TOOLS_DIR="$(dirname "$0")"
export PROJECT=libgit2
export VERSION=1.5.0
source $TOOLS_DIR/base.sh ${TARGET}

decompress

create_cmake_build_dir

cd ${CMAKE_BUILD_DIR}
export PKG_CONFIG_PATH="${DIST_DIR}/lib/pkgconfig:${DIST_DIR}/share/pkgconfig:${DIST_DIR}/lib64/pkgconfig"
export CFLAGS=$(pkg-config --cflags --static libssh2 libssl libcrypto zlib)
export LDFLAGS=$(pkg-config --libs --static libssh2 libssl libcrypto zlib)

cmake .. \
    $(toolchain) \
    -DBUILD_SHARED_LIBS=OFF \
    -DUSE_SSH=ON \
    -DBUILD_TESTS=OFF \
    -DCMAKE_BUILD_TYPE=Release \
    -DCMAKE_INSTALL_PREFIX="${DIST_DIR}" \
    -DCMAKE_PREFIX_PATH="${DIST_DIR}"


cmake --build . --target install -j8

echo "Build ${PROJECT} Successful"
