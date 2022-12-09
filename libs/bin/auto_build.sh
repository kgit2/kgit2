#!/usr/bin/env bash
set -evx
TARGET=$1
export ROOT=$(pwd)
export TOOLS_DIR="$(dirname "$0")"
${TOOLS_DIR}/build_zlib.sh $TARGET
${TOOLS_DIR}/build_openssl.sh $TARGET
${TOOLS_DIR}/build_libssh2.sh $TARGET
${TOOLS_DIR}/build_libgit2.sh $TARGET
