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

#make -s clean
../Configure \
    --release \
    --prefix=${DIST_DIR} \
    --openssldir=${DIST_DIR}/ssl \
    --with-zlib-include=${ZLIB_DIR}/include \
    --with-zlib-lib=${ZLIB_DIR}/lib \
    no-acvp-tests \
    no-buildtest-c++ \
    no-external-tests \
    no-unit-test

make -s build_sw -j6

make -s install_sw
