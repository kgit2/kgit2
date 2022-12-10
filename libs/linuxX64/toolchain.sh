#!/usr/bin/env bash

export PATH="/Users/bppleman/.konan/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/bin":$PATH

#export CC=clang -target x86_64-linux-gnu --gcc-toolchain=/opt/homebrew/opt/x86_64-unknown-linux-gnu/toolchain --sysroot=/opt/homebrew/Cellar/x86_64-unknown-linux-gnu/11.2.0/toolchain/x86_64-unknown-linux-gnu/sysroot -fuse-ld=lld

export CROSS_COMPILE="x86_64-unknown-linux-gnu-"

export CROSS_SSL_TARGET="x86_64-unknown-linux-gnu" # e.g. mipsel-sf-linux-musl
export OPENSSL_ARCH="linux-x86_64" # This value is chosen from the output of ./Configure, it is specific to OpenSSL, you can try linux-generic
export CROSS_SSL_TOOLCHAINS="/Users/bppleman/.konan/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/sysroot" #  This must match the root of where you put your built toolchains
#export CROSS_SSL_ARCH="mipsel-sf-sysv" #  This must match the toolchain name in config.mak in musl-cross-make
#export CROSS_SSL_BIN="$(dirname $(which gcc))" #  Only works if you used the activate script first
#export CROSS_SSL_CMD_PREFIX="${CROSS_SSL_BIN}/${TOOLCHAIN_TARGET}"-
#export CROSS_SSL_INC="${TOOLCHAIN_ROOT}/include"
#export CROSS_SSL_LIB="${TOOLCHAIN_ROOT}/lib"

export CFLAGS="-I${CROSS_SSL_TOOLCHAINS}/usr/include"
export LDFLAGS="-L${CROSS_SSL_TOOLCHAINS}/lib -L${CROSS_SSL_TOOLCHAINS}/usr/lib"
