set(CMAKE_SYSTEM_NAME Linux)

set(CMAKE_C_COMPILER /opt/homebrew/Cellar/x86_64-unknown-linux-gnu/11.2.0/toolchain/bin/x86_64-unknown-linux-gnu-gcc)

set(CMAKE_CXX_COMPILER /opt/homebrew/Cellar/x86_64-unknown-linux-gnu/11.2.0/toolchain/bin/x86_64-unknown-linux-gnu-g++)

set(CMAKE_FIND_ROOT_PATH /opt/homebrew/Cellar/x86_64-unknown-linux-gnu/11.2.0/toolchain/x86_64-unknown-linux-gnu/sysroot)

# adjust the default behavior of the FIND_XXX() commands:
# search programs in the host environment
set(CMAKE_FIND_ROOT_PATH_MODE_PROGRAM BOTH)

# search headers and libraries in the target environment
set(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY BOTH)
set(CMAKE_FIND_ROOT_PATH_MODE_INCLUDE BOTH)
