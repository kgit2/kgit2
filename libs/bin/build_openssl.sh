#!/usr/bin/env bash
set -evx
export ROOT=$(pwd)
export TOOLS_DIR="$(dirname "$0")"
export PROJECT=openssl
export VERSION=3.0.7
source $TOOLS_DIR/base.sh ${TARGET}

export ZLIB_DIR="${DIST_DIR}"

decompress

create_cmake_build_dir

cd ${CMAKE_BUILD_DIR}

toolchain

#make -s clean
../Configure \
    linux-x86_64 \
    --release \
    --prefix=${DIST_DIR} \
    --openssldir=${DIST_DIR}/ssl \
    no-shared \
    no-asm \
    no-acvp-tests \
    no-buildtest-c++ \
    no-external-tests \
    no-unit-test

make -s build_sw -j8 V=1

make -s install_sw
