cmake_minimum_required(VERSION 3.23)
project(native C)

set(CMAKE_C_STANDARD 11)

add_library(native native.c native.h)

set_target_properties(native PROPERTIES PUBLIC_HEADER native.h)

install(TARGETS native
#        EXPORT MyLibTargets
        LIBRARY DESTINATION lib  # 动态库安装路径
        ARCHIVE DESTINATION lib  # 静态库安装路径
        RUNTIME DESTINATION bin  # 可执行文件安装路径
        PUBLIC_HEADER DESTINATION include  # 头文件安装路径
        )
