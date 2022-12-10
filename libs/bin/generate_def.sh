#!/usr/bin/env bash
set -e
TARGET=$1
export ROOT=$(pwd)
export TOOLS_DIR="$(dirname "$0")"

export PKG_CONFIG_PATH="${ROOT}/${TARGET}/usr/lib/pkgconfig:${ROOT}/${TARGET}/usr/lib64/pkgconfig:${ROOT}/${TARGET}/share/pkgconfig"
export DEF_FILE="${ROOT}/${TARGET}/libgit2.def"

headers="git2.h git2/reflog.h git2/sys/reflog.h git2/sys/odb_backend.h git2/sys/mempack.h git2/sys/repository.h"
staticLibraries="libgit2.a libssh2.a libssl.a libcrypto.a libz.a"
libraryPaths="${ROOT}/${TARGET}/usr/lib ${ROOT}/${TARGET}/usr/lib64"
compilerOpts=$(pkg-config --cflags libgit2 --static)
linkerOpts="$(pkg-config --libs libgit2 --static)"

echo "headers = $headers" > "$DEF_FILE"
echo "staticLibraries = $staticLibraries" >> "$DEF_FILE"
echo "libraryPaths = $libraryPaths" >> "$DEF_FILE"
echo "compilerOpts = $compilerOpts" >> "$DEF_FILE"
echo "linkerOpts = $linkerOpts" >> "$DEF_FILE"
echo "" >> "$DEF_FILE"
echo "noStringConversion = git_attr_value git_mailmap_from_buffer" >> "$DEF_FILE"
echo "" >> "$DEF_FILE"
echo "---" >> "$DEF_FILE"
echo "" >> "$DEF_FILE"
echo "const char *git_attr__true  = \"[internal]__TRUE__\";" >> "$DEF_FILE"
echo "const char *git_attr__false = \"[internal]__FALSE__\";" >> "$DEF_FILE"
echo "const char *git_attr__unset = \"[internal]__UNSET__\";" >> "$DEF_FILE"
