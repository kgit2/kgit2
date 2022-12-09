#----------------------------------------------------------------
# Generated CMake target import file for configuration "Release".
#----------------------------------------------------------------

# Commands may need to know the format version.
set(CMAKE_IMPORT_FILE_VERSION 1)

# Import target "Libssh2::libssh2" for configuration "Release"
set_property(TARGET Libssh2::libssh2 APPEND PROPERTY IMPORTED_CONFIGURATIONS RELEASE)
set_target_properties(Libssh2::libssh2 PROPERTIES
  IMPORTED_LINK_INTERFACE_LANGUAGES_RELEASE "C"
  IMPORTED_LINK_INTERFACE_LIBRARIES_RELEASE "/Users/bppleman/kgit2/kgit2/libs/macosArm64/usr/lib/libssl.dylib;/Users/bppleman/kgit2/kgit2/libs/macosArm64/usr/lib/libcrypto.dylib;/Users/bppleman/kgit2/kgit2/libs/macosArm64/usr/lib/libz.dylib"
  IMPORTED_LOCATION_RELEASE "${_IMPORT_PREFIX}/lib/libssh2.a"
  )

list(APPEND _cmake_import_check_targets Libssh2::libssh2 )
list(APPEND _cmake_import_check_files_for_Libssh2::libssh2 "${_IMPORT_PREFIX}/lib/libssh2.a" )

# Commands beyond this point should not need to know the version.
set(CMAKE_IMPORT_FILE_VERSION)
