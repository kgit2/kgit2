#!/usr/bin/env zsh
ROOT=$(pwd)
DIST_DIR="$ROOT/lib/libgit2"
LIBSSH2_DIST_DIR="$ROOT/lib/libssh2"

headers="git2.h"
staticLibraries="libgit2.a"
libraryPaths="$DIST_DIR/lib $LIBSSH2_DIST_DIR/lib"
compilerOpts="-I$DIST_DIR/include"
export PKG_CONFIG_PATH="$DIST_DIR/lib/pkgconfig":$PKG_CONFIG_PATH
linkerOpts="$(pkg-config --libs libgit2 --static)"

DEF_FILE="$ROOT/lib/libgit2.def"
echo "headers = $headers" > "$DEF_FILE"
echo "staticLibraries = $staticLibraries" >> "$DEF_FILE"
echo "libraryPaths = $libraryPaths" >> "$DEF_FILE"
echo "compilerOpts = $compilerOpts" >> "$DEF_FILE"
echo "linkerOpts = $linkerOpts" >> "$DEF_FILE"
