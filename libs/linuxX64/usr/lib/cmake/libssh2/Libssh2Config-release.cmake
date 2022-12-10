#----------------------------------------------------------------
# Generated CMake target import file for configuration "Release".
#----------------------------------------------------------------

# Commands may need to know the format version.
set(CMAKE_IMPORT_FILE_VERSION 1)

# Import target "Libssh2::libssh2" for configuration "Release"
set_property(TARGET Libssh2::libssh2 APPEND PROPERTY IMPORTED_CONFIGURATIONS RELEASE)
set_target_properties(Libssh2::libssh2 PROPERTIES
  IMPORTED_LINK_INTERFACE_LANGUAGES_RELEASE "C"
  IMPORTED_LINK_INTERFACE_LIBRARIES_RELEASE "/Users/bppleman/kgit2/kgit2/libs/linuxX64/usr/lib64/libssl.a;/Users/bppleman/kgit2/kgit2/libs/linuxX64/usr/lib64/libcrypto.a;-lpthread;dl;/Users/bppleman/.konan/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/sysroot/usr/lib/libz.so"
  IMPORTED_LOCATION_RELEASE "${_IMPORT_PREFIX}/lib/libssh2.a"
  )

list(APPEND _cmake_import_check_targets Libssh2::libssh2 )
list(APPEND _cmake_import_check_files_for_Libssh2::libssh2 "${_IMPORT_PREFIX}/lib/libssh2.a" )

# Commands beyond this point should not need to know the version.
set(CMAKE_IMPORT_FILE_VERSION)
