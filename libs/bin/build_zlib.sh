#!/usr/bin/env bash
set -evx
TARGET=$1
export ROOT=$(pwd)
export TOOLS_DIR="$(dirname "$0")"
export PROJECT=zlib
export VERSION=1.2.13
source $TOOLS_DIR/base.sh ${TARGET}

decompress

create_cmake_build_dir

cd ${CMAKE_BUILD_DIR}

cmake .. $(toolchain) -DCMAKE_INSTALL_PREFIX=${DIST_DIR}

cmake --build . --target install -j8
