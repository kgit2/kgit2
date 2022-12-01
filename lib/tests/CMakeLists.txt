cmake_minimum_required(VERSION 3.23)
project(tests)

set(CMAKE_C_STANDARD 17)

add_executable(tests main.c args.h args.c common.h common.c)

set(
        CMAKE_PREFIX_PATH
        ${CMAKE_SOURCE_DIR}/../build/dist/libgit2
        ${CMAKE_SOURCE_DIR}/../build/dist/libssh2
        /opt/homebrew/opt/openssl@3
)

find_package(PkgConfig REQUIRED)

pkg_check_modules(GIT REQUIRED IMPORTED_TARGET libgit2 libssh2 openssl libssl libcrypto zlib)
find_library(CF CoreFoundation)
find_library(SEC Security)
find_library(ICONV iconv)

message(${GIT_STATIC_LIBRARIES})
message(${GIT_STATIC_LDFLAGS})
message(${ICONV})

target_link_libraries(tests PkgConfig::GIT ${CF} ${SEC} ${ICONV})
target_include_directories(tests PUBLIC ${GIT_INCLUDE_DIRS})
